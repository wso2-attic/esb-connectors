Product: Integration tests for WSO2 ESB ProWorkflow connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

Steps to follow in setting integration test.

 1. Download WSO2 ESB 4.9.0-ALPHA from official website.

 2. Set up a new ProworkFlow trial account by completing the wizard in https://www.proworkflow.com/signup/trial_signup.cfm url.
	Then, login to the account and navigate to settings -> API Settings and obtain the Customer Key.

 3. Follow the below mentioned steps for adding valid certificate to access ProWorkflow API over https.

	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://api.proworkflow.net
	ii)  Go to new ESB 4.9.0 folder and place the downloaded certificate in both "<ESB_Connector_Home>/repository/resources/security/" and "{ESB_Connector_Home}/proworkflow-connector/proworkflow/proworkflow-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" folders.
	iii) Navigate to "<ESB_Connector_Home>/repository/resources/security/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" 
				
		 This command will import ProWorkflow certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from ProWorkflow with the extension. (e.g. ProWorkflow.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. ProWorkflow)
				
	iv)  Navigate to "{ESB_Connector_Home}/proworkflow/proworkflow-connector/proworkflow-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 
				
		 This command will import ProWorkflow certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from ProWorkflow with the extension. (e.g. ProWorkflow.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. ProWorkflow)

 4. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 5. Update the properties in 'proworkflow.properties' file at location "{ESB_Connector_Home}/proworkflow/proworkflow-connector/proworkflow-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl 		          	 - Use the API url as "https://api.proworkflow.net".
	ii)		apiKey 		          	 - Use the Customer Key obtained in step 2.
	iii)	username			  	 - The username of the account created in step 2. Instead you can provide the account email here.
	iv)		password			  	 - The password of the account created in step 2.
	v)		city				  	 - Use a valid city.
	vi)		country				  	 - Use a valid country.
	vii)	email				  	 - Use a valid email address.
	viii)	phoneNumber			  	 - Use a valid phone number. (e.g. 0714600270)
	ix)		zipCode				  	 - Use valid zip or postal code.
	x)		lineItemCost		  	 - Use a valid cost amount for line item. (e.g. 12500)
	xi)		fixedItemsCost	  	 	 - Use a valid cost amount for line item. (e.g. 12000)
	xii)	quoteDiscountValue	  	 - Use a valid percentage value for quote discount. (e.g. 5)
	xiii)	updateQuoteDiscountValue - Use a valid percentage value for quote discount (e.g. 10). Use a different value than the quoteDiscountValue parameter value. 
	xiv)	taxRate					 - Use a valid percentage value for quote tax rate. (e.g. 15)

 6. Make sure that the proworkflow connector is set as a module in esb-connectors parent pom.
         <module>proworkflow/proworkflow-connector/proworkflow-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install
