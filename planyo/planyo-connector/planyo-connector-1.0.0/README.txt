Product: Integration tests for WSO2 ESB Planyo connector

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
 
 2. Deploy relevant patches, if applicable.
 		
 3. Set up a new Planyo account by following the wizard in https://www.planyo.com/login/signup.php url. 
	Then, follow the steps in https://www.planyo.com/api.php to obtain the API key.

 4. Follow the below mentioned steps for adding valid certificate to access Planyo API over https.

	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://api.planyo.com
	ii)  Go to new ESB 4.8.1 folder and place the downloaded certificate in both "<ESB_HOME>/repository/resources/security/" and "{PLANYO_CONNECTOR_HOME}/planyo-connector/planyo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" folders.
	iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" 
				
		 This command will import Planyo certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Planyo with the extension. (e.g. planyo.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Planyo)
				
	iv)  Navigate to "{PLANYO_CONNECTOR_HOME}/planyo-connector/planyo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.
	
				keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 
				
		 This command will import Planyo certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		
		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Planyo with the extension. (e.g. planyo.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Planyo)

 5. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{PLANYO_CONNECTOR_HOME}/planyo-connector/planyo-connector-1.0.0/org.wso2.carbon.connector/repository/".

 6. Set up the Planyo account as mentioned below.
 
		i)	Log in to the newly created Planyo account, create a site using the given wizard. After completion, switch the UI to advanced using the toggle button in the top left corner.
		
		ii) Add a new user to the account.
			  Consider the following steps to add the user.
			  - Navigate to Customers -> Users created by admin / Add new user in Planyo account.
			  - Add the user by giving values for the mandatory fields. Save the user email address to be used in step 7(iii).
			
		iii)Add a new resource to the system and add custom properties to the created resource.
			  Consider the following steps to add a new resource and custom property.
			  - Navigate to Settings -> Add new resource in the Planyo account.
			  - Add the resource by giving required details. Retrieve the resource id from the url and save it to be used in step 7(iv).
			  - Navigate to Settings -> Resource settings -> General info -> and click on the link named 'Click here to add additional properties to this and other resources'.
			  -	Add the new custom property by giving the name and value in 'Enter new item' section. Use the default values for the other fields. 
			  - Navigate to the created resource from Settings -> <Resource_Name> -> General info and provide a value for the added property. Save the property name and the value given later to be used in 7(v) and 7(vi) steps.
			  
		iv) Create a new Reservation by Navigating to Reservations -> Make Reservation and selecting a Resource. Then complete the wizard and make sure to include the user created in 6(iii) as the client. You can use the provided 'findClient' option to find the created User.
			Once the Reservation is created, navigate to the Reservation by clicking on the Reservation ID and confirm the Reservation by clicking 'Confirm'.
			
		v)	Make sure to create at least one voucher by using following steps.
				- Navigate to Settings -> Vouchers in the Planyo account.
				- Add the voucher by giving required details in the 'Enter new item' section.
				- Make sure to assign this voucher to created resource in step 6(iii).
		
 7. Update the properties in 'planyo.properties' file at location "{PLANYO_CONNECTOR_HOME}/planyo-connector/planyo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl 		          - Use the API URL as "http://api.planyo.com".
	ii)		apiKey 		          - Provide the Planyo API key.
	iii) 	email		          - Use the email address of the added user in step 6(ii).                    
	iv)		baseResourceId  	  - Use the ID of the added resource saved in step 6(iii).
	v)		resourcePropertyName  - Use the added custom property name for the resource saved in step 6(iii).
	vi)		resourcePropertyValue - Use the added custom property value for the resource saved in step 6(iii).
	
 8. Navigate to "{PLANYO_CONNECTOR_HOME}/planyo-connector/planyo-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
