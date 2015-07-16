Product: Integration tests for WSO2 ESB Zohobooks connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04, Mac OSx 10.9
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB WSO2 ESB 4.9.0-BETA-SNAPSHOT by navigating the following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/.

 2. Extract the certificate from browser by navigating to "https://books.zoho.com" and place the certificate file in following locations. 

	i)  "<ZOHOBOOKS_CONNECTOR_HOME>/zohobooks-connector/zohobooks-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "zohobooks"' in command line to import zohobooks certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from zohobooks with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "<ESB_HOME>/repository/resources/security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "zohobooks"' in command line to import zohobooks certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from zohobooks with  the extension, change it accordingly. Remove the copied certificate.

 3. ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
		
		<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
										  
		<messageBuilder contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

		<messageFormatter contentType="text/html"                             
							  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="text/html"                                
							  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>


 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 5. Make sure that zohobooks is specified as a module in ESB_Connector_Parent pom.
        <module>zohobooks/zohobooks-connector/zohobooks-connector-1.0.0/org.wso2.carbon.connector</module>

 6. Create a Zohobooks trial account and derive the API Key.
	i) 	Using the URL "https://www.zoho.com/books/signup/" create a Zohobooks trial account.
	ii)	Obtain the auth token and organization ID as instructed in "https://www.zoho.com/books/api/v3/".
	
 7. Prerequisites for ZohoBooks Connector Integration Testing
	i)	Navigate to https://books.zoho.com/app#/settings/preferences , make sure to enable all the modules and to uncheck 'Set max hours/day' under 'General' tab.
	ii) Navigate to the URL "https://books.zoho.com/app#/accountant/chartofaccounts" and create at least one accountant with the Account type 'Expense'.
	iii) Navigate to "Users" under the settings, and invite to new user as a timesheet staff by clicking "Invite User" button.
	iv)Log in to newly created user's email (created in step 7 iii) ) and click "Join Account" URL to join with zohobooks account.

 8. Update the Zohobooks properties file at location "<ZOHOBOOKS_CONNECTOR_HOME>/zohobooks-connector/zohobooks-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created Zohobooks account (https://books.zoho.com).
	ii) 	authToken						-   Use the access token obtained under step 6 ii).
	iii)	organizationId					-	Use the organization ID obtained under step 6 ii).
	iv)*    itemNameMandatory				-	Use a unique string for the item name (Used for createItem - Mandatory testcase).
	v)*		itemNameOptional				- 	Use a unique string for the item name (Used for createItem - Optional testcase).
	vi)		rate							-   Use a numeric value, either integer or decimal (limit to 1 decimal place).
	vii)	unit							-	Use a valid measuring unit (E.g. Kgs, Nos).
	viii)   website							-	Use a properly formed website name (E.g. www.sample.com).
	ix)	    companyName						-	Use a valid string as the company name.
	x)*		contactNameMandatory			-	Use a unique string for the contact name (Used for createContact - Mandatory testcase).
	xi)*	contactNameOptional			    -	Use a unique string for the contact name (Used for createContact - Optional testcase).
	xii)*	contactPersonFirstName			-	Use a unique string for the contact person name (Used for createContact - Mandatory testcase).
	xiii)*	contactPersonLastName			-	Use a unique string for the contact person name (Used for createContact - Mandatory testcase).
	xiv)	contactPersonEmail				- 	Use a valid email address (no need to be a real one).
	xv)*	invoiceNumber					-	Use a unique valid string for the invoice number.
	xvi)	invoiceDueDate					-	Use a valid future date in the format of 'yyyy-mm-dd' (e.g. 2015-02-23).
	xvii)	purchaseDate					-	Use a valid date for purchase order creation in the format of 'yyyy-mm-dd' (e.g. 2015-02-23).
	xviii)	paymentDate						-	Use a valid date for the payment in the format of 'yyyy-mm-dd' (e.g. 2015-02-23).
	xix)	paymentAmount					-	Use a numeric value, either integer or decimal for the payment amount .
	xx)		paymentDescription				-	Use a appropriate description for the payment.
	xxi)	paymentReferenceNumber			-	Use a valid preferred string for the reference number of the payment.
	xxii)	notes							-	Use a preferred string for the note in the contact. 
	xxiii)* projectName						-   Use a unique string as the project name.
	xxiv)* 	projectNameOptional				-	Use a unique string as the project name to create project with optional parameters.
	xxv)	projectDescription				-	Use a string as the project description.
	xxvi)*	userName						-	Use a unique and valid string as the user name (e.g. jamesgoslin).
	xxvii)*	email							-	Use a unique and valid email address to create user.
	xxviii)	userRole						-	Use a valid user role value (e.g. timesheetstaff).
	xxix)*	taskName						-	Use a unique string as the task name.
	xxx)*	taskNameOpt						-   Use a unique string as the task name to create task with optional parameters.
	xxxi)	taskDescription					-   Use a string value as the task description.
	xxxii)	taskRate						-	Use a valid numeric value as the task rate	(e.g. 30.0).
	xxxiii)	logDate							-	Use a valid date string for the time entries log date with the format of 'yyyy-mm-dd' (e.g. 2014-05-30).
	xxxiv)	logTime							-   Use a valid time for the time entries log time with the format of 'hh:mm' (e.g.08:30)
	xxxv)	isBillable						-	Use a boolean value ("true" or "false") to assign the time enties as billable or not.
	xxxvi)	timeEntryNotes					-   Use a string as the time entry note (e.g. Foo Bar Baz).
	xxxvii)	taskUserId						-	Place the user ID created under step 7 ii).
	
	* Values need to be changed for each execution of the Test Suite. Please make sure the values are unique in the context of the same account.
	
 9. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install