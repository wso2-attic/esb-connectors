Product: Integration tests for WSO2 ESB CleverTim connector

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

 1. Follow the below mentioned steps for adding valid certificate to access clevertim API over https.

	i) Extract the certificate from browser by navigating to https://www.clevertim.com/en/ and place the certificate file in following location.
	   "{CLEVERTIM_CONNECTOR_HOME}/clevertim-connector/clevertim-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	  
	ii) Navigate to "{CLEVERTIM_CONNECTOR_HOME}/clevertim-connector/clevertim-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import Clevertim certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from clevertim, change it accordingly. (e.g. -.clevertim.com)
			   CERT_NAME is name of the certificate. (e.g. clevertim)
	   
	iii)Go to new ESB 4.8.1 folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import Clevertim certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from clevertim, change it accordingly. (e.g. -.clevertim.com)
			   CERT_NAME is name of the certificate. (e.g: clevertim)
		
 2. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
	
	Message Formatters :-
		
	<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
							  
	<messageFormatter contentType="text/html"                             
					  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
					  
	<messageBuilder contentType="application/json"
					  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

	<messageBuilder contentType="text/html"                                
					  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
	
 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{CLEVERTIM_CONNECTOR_HOME}/clevertim-connector/clevertim-connector-1.0.0/org.wso2.carbon.connector/repository/".
	
 4. Create a CleverTim account and derive the API key:
	i) 	 Using the URL "http://www.clevertim.com/en/" create a Clevertim Premium Free Trial account.
	ii)  Login to the created Clevertim account and derive the API Key by navigating to Accounts >> My Info & settings and by clicking on 'Generate API' key button.

 5. Update the Clevertim properties file at location "{CLEVERTIM_CONNECTOR_HOME}/clevertim-connector/clevertim-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl - Use the API URL as "https://www.clevertim.com".
	ii) 	apiKey - API key that is obtained under Step 4 ii).
	iii) 	caseName - Provide a preferred name for a case.
	iv) 	description - Provide a proper description. Any string can be provided.
	v) 		comment - Provide a preferred name for a comment.
	vi)     opportunityName - Provide a preferred name for an opportunity.
	vii) 	opportunityDesc - Provide a proper description for an opportunity.	
	viii) 	contactFirstName - Provide a proper value for a contact's first name. Any string is applicable.
	ix)	    contactLastName - Provide a proper value for a contact's last name. Any string is applicable.
	x)		contactTitle - Provide a proper value for a contact's title. Any string is applicable.
	xi)		taskName - Provide a preferred name for a task.
	xii)	taskLocation - Provide a preferred location for a task. Use a string other than 'Colombo'.
	xiii)	companyName - Provide a preferred name for the Company.

 6. Navigate to "{CLEVERTIM_CONNECTOR_HOME}/clevertim-connector/clevertim-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

 NOTE : 
	  -Clevertim Free trial account is only valid for 30 days.
	  
