Product: Integration tests for WSO2 ESB Constant Contact connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

   Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1
 
STEPS:

1. Download ESB 4.8.1 from official website.

2. Deploy relevant patches, if applicable.

3. Follow the below mentioned steps to create a new Constant Contact account:

	i) 	 Navigate to the following url and create an account in Constant Contact: https://www.constantcontact.com/signup.jsp.
	ii)  Navigate to the following url and create a account in Constant Contact API management: https://constantcontact.mashery.com/member/register.
	iii) Navigate to https://constantcontact.mashery.com/apps/register and register a new application.
	iv)  Go to 'API Keys', retrieve the Key and Secret and save it for further use.
	v) 	 Generate the API Token as mentioned in the following URL.
			http://developer.constantcontact.com/docs/authentication/authentication.html
	
4. Follow the below mentioned steps to add valid certificate to access ConstantContact API over https.

	i) 	Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://api.constantcontact.com/' 
	ii)  Go to new ESB 4.8.1 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
		 "{ConstantContact_Connector_Home}/constantcontact-connector/constantcontact-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"
				
		 This command will import ConstantContact certificate into keystore. 
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from ConstantContact with the extension. (e.g. constantcontact.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. ConstantContact)

	iv) Navigate to "{ConstantContact_Connector_Home}/constantcontact-connector/constantcontact-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.
		
				keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 
				
		 This command will import constantcontact certificate in to keystore. Give "wso2carbon" as password.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from ConstantContact with the extension. (e.g. constantcontact.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. ConstantContact)
	   
5. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{ConstantContact_Connector_Home}/constantcontact-connector/constantcontact-connector-1.0.0/org.wso2.carbon.connector/repository/".
	
6. Update the property file constantcontact.properties found in {ConstantContact_Connector_Home}/constantcontact-connector/constantcontact-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:
	
	i)		apiUrl 							- 	API endpoint to which the service calls are made. e.g. https://api.constantcontact.com.
	ii)		apiKey 							- 	Use the API key obtained in Step 3 - iv.
	iii)	apiToken 						-  	Use the apiToken obtained in Step 3 - v.
	iv)		subject 						-	Use a valid string value for the subject of the email.
	v)		email 							- 	Use the email address for which the Constant Contact account is created.
	vi)		status 							- 	Use the value 'ACTIVE'.
	vii)	modifiedSince 					-	Use a valid past date according to format (YYYY-MM-DDTHH:mm:SS.sssZ).
	viii)	limit 							- 	Use a positive integer value for the page limit.
	ix)		scheduledDate 					- 	Use a valid future date according to format (YYYY-MM-DDTHH:mm:SS.sssZ) for scheduling emails.
	x)		trackCampaignId 				- 	Use an already created campaign id which should have sent emails and that emails should be opened and clicked.
	xi)		createdSince 					- 	Use a valid past date according to format (YYYY-MM-DDTHH:mm:SS.sssZ).
	xii)	campaignName 					-   Use a unique and a valid string value for the campaign name.
	xiii)	campaignNameOptional 			- 	Use a unique and a valid string value for the campaign name.
	xiv)	contactEmailAddresses 			- 	Use a unique and a valid email address.
	xv)		contactEmailAddressesOptional 	- 	Use a unique and a valid email address.
	xvi)	contactEmailAddressesUpdate 	- 	Use a unique and a valid email address.

	Note:	The property values of campaignName, campaignNameOptional, contactEmailAddresses, contactEmailAddressesOptional, contactEmailAddressesUpdate should be changed to unique different values for each integration execution.

7. Navigate to "{ConstantContact_Connector_Home}/constantcontact-connector/constantcontact-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
