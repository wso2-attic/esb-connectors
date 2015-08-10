Product: Integration tests for WSO2 ESB Cashboard connector

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

 1. Download ESB 4.9.0-BETA-SNAPSHOT by navigating to the following URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/
 
 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

        <messageFormatter contentType="application/pdf" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageBuilder contentType="application/pdf" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 4. Using the URL "https://register.cashboardapp.com/" create a Cashboard trial account and note the subdomain used in the registration.

 5. Prerequisites for Cashboard Connector Integration Testing.
	i)Create a Project by navigating to 'https://{subdomain}.cashboardapp.com/provider/projects/new' in the created Cashboard account and keep the projectId (id displays in the url when the project is selected) for further reference.
   
 6. Follow the below mentioned steps to add valid certificate to access Cashboard API over https.

    i)   Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://{subdomain}.cashboardapp.com'.
    ii)  Go to new ESB 4.9.0 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
         "<CASHBOARD_CONNECTOR_HOME>/cashboard-connector/cashboard-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

         keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import Cashboard certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Cashboard with the extension. (e.g. cashboard.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. cashboard)

    iv) Navigate to "<CASHBOARD_CONNECTOR_HOME>/cashboard-connector/cashboard-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

         keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Cashboard certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Cashboard with the extension. (e.g. cashboard.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. cashboard).
 
 7. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 8. Make sure that Cashboard is specified as a module in ESB Connector Parent pom.
        <module>cashboard/cashboard-connector/cashboard-connector-1.0.0/org.wso2.carbon.connector</module>
 
 9. Update the Cashboard properties file at location "<CASHBOARD_CONNECTOR_HOME>/cashboard-connector/cashboard-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)    	apiUrl                 		- The API URL specific to the domain of the created account. e.g. https://api.barnselectronics.cashboardapp.com
   ii)	 	emailAddress				- Email address that is used to create the Cashboard trial account under Step 4.
   iii)  	password			 		- Password that is used to create the Cashboard trial account under Step 4.
   iv)	 	subdomain			 		- Subdomain of the Cashboard trial account that is created under Step 4.
   v)	 	clientCompanyName			- Use a valid string value as company name.
   vi)   	address						- Use a valid string value as address.
   vii)  	city						- Use a valid string value as city.
   viii) 	state               		- Use a valid string value as state.
   ix)	 	zip                   		- Use a valid value for zip value.
   x) 	 	firstName              		- Use a valid string value as first name.			
   xi)   	clientEmailAddress     		- Use any email address which is correctly formatted.			
   xii)  	employeeEmail      			- Use any email address which is correctly formatted. This should be a different email than 'clientEmailAddress'.			
   xiii) 	emailAddressOptional  		- Use any email address which is correctly formatted. This should be a different email than 'clientEmailAddress' and 'employeeEmail'.			
   xiv)  	projectId              		- Use a valid project Id that is created under Step 5 i).
   xv) 	 	rank                   		- Use a valid rank value. e.g:- 1  
   xvi)  	title                  		- Use a valid string value as project list title.
   xvii) 	isArchived             		- Use a valid boolean value for archive status. e.g:-  false
   xviii)	estimateName           		- Use a valid string value as estimate name.
   xix)  	introText              		- Use a valid string value as the agreement text.  
   xx)   	agreementText          		- Use a valid string value as agreement text of the estimate. 
   xxi)  	typeCode               		- Use a valid type code value. e.g:- 0 , 1 or 2
   xxii) 	clientTypeCompany      		- Use 'Company' as the client type.
   xxiii)	notes                  		- Use a valid string as the note. 
   xxiv) 	discountPercentage     		- Use a valid double value as the discount percentage. e.g:- 5.0     
   xxv)  	hasBeenSent            		- Use a valid boolean value. e.g:- false      
   xxvi) 	hasBeenPaid            		- Use a valid boolean value. e.g:- false           
   xxvii)	updateAdress           		- Use a valid string as the address. This should be a different value than the 'address'.          
   xxviii)	updateNotes            		- Use a valid string as the updated note. This should be a different value than the 'notes'.               
   xxix) 	updateDueDate          		- Use a valid date in the format 'YYYY-MM-DD'. e.g:- 2016-06-10   
   xxx)  	updateCreatedOn       	 	- Use a valid date in the format 'YYYY-MM-DD'. e.g:- 2016-06-10   
   xxxi) 	updateDiscountPercentage 	- Use a valid double value as the discount percentage. Use a value different to 'discountPercentage'. e.g:- 2.0 
   xxxii)	lastName               		- Use a valid string as the lastname.  
   xxxiii)	telephone             		- Use a valid phone number. e.g:- 0715837733
   xxxiv)	url               			- Use a valid url. e.g:- www.google.com
 
   Note - The property values of clientCompanyName,clientEmailAddress,employeeEmail,emailAddressOptional and the title should be changed to unique different values for each integration execution.  

 10. Navigate to "<CASHBOARD_CONNECTOR_HOME>/cashboard-connector/cashboard-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
