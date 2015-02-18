Product: Integration tests for WSO2 ESB Clio connector

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
 
 2. Follow the below mentioned steps for adding valid certificate to access Clio API over https

	i) Extract the certificate from browser by navigating to https://app.goclio.com and place the certificate file in following location.	
	   "{Clio_Connector_Home}/clio-connector/clio-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	   
	   Note: It is recommended that the certificate be saved in Base64-Encoded certificate file with '.cer' extension to prevent issues arising due to SSL Handshake.
	  
	ii) Navigate to "{Clio_Connector_Home}/clio-connector/clio-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import Clio certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from Clio, change it accordingly. (e.g. -.clio.com)
			   CERT_NAME is name of the certificate. (e.g. clio)
	   
	iii)Go to new ESB 4.8.1 folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import Clio certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from Clio, change it accordingly. (e.g. -.clio.com)
			   CERT_NAME is name of the certificate. (e.g: clio) 

 3. Deploy relevant patches, if applicable and the ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
		
		<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
										  
		<messageBuilder contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

		<messageFormatter contentType="text/html"                             
							  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="text/html"                                
							  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>


 4. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Clio_Connector_Home}/clio-connector/clio-connector-1.0.0/org.wso2.carbon.connector/repository/".

 5. Create a Clio trial account and derive the API Key.
	i) 		Using the URL "http://www.goclio.com/sign-up/" create a Clio trial account.
	ii)		Create an application as described in "http://api-docs.clio.com/v2/#create-a-clio-application".
	iii)	Obtain the access token as instructed in "http://api-docs.clio.com/v2/#obtaining-authorization".

 6. Update the Clio properties file at location "{Clio_Connector_Home}/clio-connector/clio-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created Clio account (https://app.goclio.com).
	ii) 	accessToken						-   Use the access token obtained under step 5 iii).
	iii)	lastName						-	Use a valid string as the last name of the contact.
	iv)		firstName						-	Use a valid string as the first name of the contact.
	v)		contactType						- 	Use a valid contact type (e.g:- Person).
	vi)		title							- 	Use a valid title for the person contact.
	vii)	prefix							-   Use a valid prefix for the person contact (e.g:-Mr).
	viii)	status							-	Use a valid status for a matter (e.g:-Open).
	ix)	    descriptionMandatory			-	Use a valid string as the description.
	x)	    descriptionOptional				-	Use a valid string as the description.
	xi)	    billable						-	Use either 'true' or 'false'.
	xii)	closeDate						-	Use a valid future date in the format of 'yyyy-mm-dd' (e.g:-2015-01-23).
	xiii)	openDate						-	Use a valid future date in the format of 'yyyy-mm-dd' (e.g:-2015-01-23).
	xiv)	billId							-	Use a valid bill Id. (Create a Bill in the Web Application by navigating to https://app.goclio.com/bills, create a Bill and get its ID from the URL).
	xv)	    limit							- 	Use a valid integer as the maximum number of records to be returned.
	xvi)	userId							-	Use a valid user Id. (Navigate to https://app.goclio.com/users, click on the user and get the ID from the URL).
	xvii)	taskNameMandatory				-	Use a valid string as the task name.
	xviii)	taskPriorityMandatory			-	Use a valid priority value ("High", "Normal" or "Low").
	xix)	taskNameOptional				-	Use a valid string as the task name.
	xx)		taskDescriptionOptional			-	Use a valid string as the task description.
	xxi)	taskDueAtOptional				-	Use a valid future date in the format of 'yyyy-mm-dd' (e.g:-2015-01-23).
	xxii)	taskIsPrivateOptional			-	Use either 'true' or 'false'.
	xxiii)	updatedTaskName					-	Use a valid string as the task name.Note this value must be different to the value given for 'taskNameMandatory',
	xxiv)	updatedTaskIsPrivate			-	Use either 'true' or 'false'.
	xxv)    updateFirstName					-	Use a valid string as the first name of the contact.This value must be different than the value given for 'firstName'.
	xxvi)	updateLastName					-	Use a valid string as the last name of the contact.This value must be different than the value given for 'lastName'.
	
 7. Navigate to "{Clio_Connector_Home}/clio-connector/clio-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

		