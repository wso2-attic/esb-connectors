Product: Integration tests for WSO2 ESB Shippo connector

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
 
	<messageFormatter contentType=”application/json”
							  class=”org.apache.synapse.commons.json.JsonStreamFormatter”/>
	<messageBuilder contentType=”application/json”
							class=”org.apache.synapse.commons.json.JsonStreamBuilder”/>
 
 3. Follow the below mentioned steps for adding valid certificate to access Shippo API over https. If the certificates are already available in keystores, you can skip this step.

	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://api.goshippo.com
	ii)  Go to new ESB 4.8.1 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
		 "{SHIPPO_CONNECTOR_HOME}/shippo-connector/shippo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" folders.
	iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"
				
		 This command will import Shippo certificate into keystore. 
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Shippo with the extension. (e.g. shippo.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Shippo)
				
	iv) Navigate to "{SHIPPO_CONNECTOR_HOME}/shippo-connector/shippo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.
				
				keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME"
				
		This command will import Shippo certificate into keystore. 
		To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		
		NOTE : 	CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Shippo with the extension. (e.g. shippo.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Shippo)

 4. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to "{SHIPPO_CONNECTOR_HOME}/shippo-connector/shippo-connector-1.0.0/org.wso2.carbon.connector/repository/" folder.

 5. Follow the bellow mentioned steps to get username and password for basic HTTP authentication:
		
		i) 	Create a Shippo account using URL https://goshippo.com/register/ by giving required values for relevant fields and proceed.

			Note: Copy and save the username and password used to create the Shippo account.
		
		ii) This will take you to Shippo dashboard and ask to verify the account by sending a verification email to the given email address.
			When verification is done, dashboard data will be previewed.
			
 6. Update the Shippo properties file at location "{SHIPPO_CONNECTOR_HOME}/shippo-connector/shippo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl					- Use the API URL as "https://api.goshippo.com".
	ii)		username				- Username obtained in step 5.
	iii) 	password				- Password obtained in step 5.
	iv)		description				- Use a valid string as the customs declaration's description. 
	v)		contentsExplanation		- Use a valid string as the customs declaration's content explanation.
	vi) 	company			 		- Use a valid string as the name of the company.
	vii)	email					- Use a valid email address.
	
	Note:-  Give a valid different email address from username for email property.
	
 7. Navigate to "{SHIPPO_CONNECTOR_HOME}/shippo-connector/shippo-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
