Product: Integration tests for WSO2 ESB FreeAgent connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

Note:
	This test suite can be executed based on two scenarios.
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new FreeAgent account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA zip from the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.
 
 2. Deploy relevant patches, if applicable.

 3. Extract the certificate using the relevant apiUrl (e.g. "https://api.sandbox.freeagent.com") and place the certificate file in following locations. 

	i)  "<FREE_AGENT_CONNECTOR_HOME>/freeagent-connector/freeagent-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "freeagent"' in command line to import FreeAgent certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from FreeAgent with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "wso2esb-4.9.0-ALPHA/repository/resources/security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "freeagent"' in command line to import FreeAgent certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from FreeAgent with  the extension, change it accordingly. Remove the copied certificate.

 4. The ESB should be configured as below;
 
	Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).
   
    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
	
    <messageBuilder contentType="text/html"	class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 5. Follow the below steps to create a FreeAgent account.

	i)	Navigate to "https://www.freeagent.com/" and click on "Try FreeAgent" button. This account will be expired after the trial period of 30 days.
    ii) Enter the required details and complete the account creation. 
		
 6. Follow the steps in the below link to obtain the access token.

	i) 	Naviagte to "https://dev.freeagent.com/" and register a new app.
			Use the URL "https://dev.freeagent.com/docs/oauth#how-oauth-2-0-works"  to create a app and to obtain the accesstoken.
				

 7. Update the FreeAgent properties file at location "{freeagent_connector_Home}/freeagent-connector/freeagent-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl						-	Use the API URL as "https://api.sandbox.freeagent.com".
	ii)		accessToken					-	Place the access token obtained in step 6 [i].
	iii)	taskName					-	Name of the task for the mandatory test case(Unique).
	iv)		taskNameOpt					-	Name of the task for the optional test case(Unique).
	v)		currency					-	Currency type related to projects and the invoices(eg:USD) .
	vi)		projectName					-	Name of the project for the mandatory case. 
	vii)	projectNameOptional			-	Name of the project for the optional case.
	viii)	ProjectNormalBillingRate	-	Billing rate of the project.
	ix)		projectHoursPerDay			-	Number of hours working per day related to the project.
	x)		ProjectBudgetUnits			-	Budget unit of the project.
	xi)		projectView					-	A filter to view projects according to the category it falls(eg :active) .
	xii)	projectBudget		        -	Budget of the project. 
	xiii)	invoiceDatedOn				-	Date mentioned for the invoice.
	xiv)	invoicePaymentTermsInDays	-	Number of days for the invoice payment .
	xv)		invoiceDiscountPercent		-	Percentage of the discount for a invoice.
	xvi)	invoiceSort					-	A parameter to sort invoices under different categories.
	xvii)	invoiceView					-	A filter to view invoices. 
	xviii)	firstName					-	First name of the contact.
	xix)	lastName					-	Last name of the contact.
	xx)		organisationName			- 	Name of the organization which the contact working for. 
	xxi)	email						- 	Email address of the contact.
	xxii)	datedOn						-	Date mentioned for the times slip.

		
 8.Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
                 $ mvn clean install


 NOTE : Following are the credentials for the FreeAgent account used for integration tests.
 
	    email=sasitest12@gmail.com
	    password=yasasi123
		
