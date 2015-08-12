Product: Integration tests for WSO2 ESB Jotform connector

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

 3. Follow the below mentioned steps to add valid certificate to access Jotform API over https.

    i)   Extract the certificate from 'https://api.jotform.com'.
    ii)  Place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
         "<JOTFORM_CONNECTOR_HOME>/jotform-connector/jotform-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

         keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import Jotform certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Jotform with the extension. (e.g. jotform.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. jotform)

    iv) Navigate to "<JOTFORM_CONNECTOR_HOME>/jotform-connector/jotform-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

         keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Jotform certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Jotform with the extension. (e.g. jotform.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. jotform).
 
 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 5. Make sure that jotform is specified as a module in ESB_Connector_Parent pom.
 	<module>jotform/jotform-connector/jotform-connector-1.0.0/org.wso2.carbon.connector</module>

 6. Create a Jotform account and login.
	i) 	Go to your user account api section by navigating to the URL "http://www.jotform.com/myaccount/api".
	ii)	Click "Create New Key" to generate a api key.
	
 7. Prerequisites for Jotform Connector Integration Testing

	i) 	Navigate to the URL "http://www.jotform.com/myforms", create a form.
	ii) Add a submission to the previously created form.
 
 8. Update the Jotform properties file at location "<JOTFORM_CONNECTOR_HOME>/jotform-connector/jotform-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL which the service calls are made. (e.g. https://api.jotform.com).
	ii) 	apiKey							-   Use the API key obtained under step 6 ii).
	iii)	formId							-   Id of the form which is created under step 7 i).
	iv)		submissionId					-	Id of the submission which is submitted in step 7 ii).

 8. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install

		