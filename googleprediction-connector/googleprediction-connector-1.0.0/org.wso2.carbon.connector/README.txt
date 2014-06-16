Product: Integration tests for WSO2 ESB GooglePrediction connector
==================================================================

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

			    
 1. Create a GooglePrediction account and get user credentials according to below steps:

	i)  Create a test gmail account and navigate to url "https://console.developers.google.com/project" 
	    (Login to the application using test mail account credentials.)

	ii) When you logged successfully in step (i) you can create new project in google developper console.
		(Click create new project button and provide new project name and project id.)

	iii) Once step (ii) succeeded you will be navigated to project dashboard. 
	     (Click Enable API button) 

	iv) Enable the Prediction API for your project. Then navigate back to project Dashboard. 
	    (click Overview Button in left handside menu list)	
	
	v) In project dashboard click create bucket to create a storage bucket for your project. Then Click "enable billing link" which appears as in browser popup.
	   	   
	vi) Confirm step (v) and fill "set up a billing profile" details. In "set up billing profile", select the account type as "individual" (Provide valid credit card details). Then submit and enable billing for your account.
		
	vii) If step (vi) succeeded. Navigate back to your project dashboard and click create new bucket again. (Give unique name and create a bucket) 
		(Note: keep project number and bucket name for future references.)
		  
	viii) Then click your created bucket under "cloud storage" and upload the file to train the system.
		  (sample file can be found at https://developers.google.com/prediction/docs/language_id.txt)
		  [NOTE: The same file name needs to be updated in googleprediction property file.]	  
		
 2. Make sure the latest "integration-base" project is placed at "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/".

 3. Navigate to "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      	$ mvn clean install
		
 4. Navigate to "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/" and delete existing "src","target" folders and delete pom.xml file.		

 5. Then copy the 'src' folder and pom.xml from the provided GooglePrediction connector source (org.wso2.carbon.connector) to the location "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/". When running integration tests, uncomment the following code snippets in pom.xml and save the changes:

	<parent>
	    <groupId>org.wso2.esb</groupId>
	    <artifactId>esb-integration-tests</artifactId>
	    <version>4.8.1</version>
	    <relativePath>../pom.xml</relativePath>
	</parent>
 
	<dependency>
	    <groupId>org.wso2.esb</groupId>
	    <artifactId>org.wso2.connector.integration.test.base</artifactId>
	    <version>4.8.1</version>
	    <scope>system</scope>
	    <systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>
	</dependency>

  6. Make sure the GooglePrediction test suite is enabled (as given below) and all other test suites are commented out in the following file: "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"
	
    <test name="GooglePrediction-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.googleprediction"/>
        </packages>
    </test> 
	
  7. Follow the below mentioned steps for adding valid certificate to access GooglePrediction API over https

	i) Extract the certificate from browser by navigating to apiUrl["https://www.googleapis.com"] and place the certificate file in following location.
	   "<Integration_Test>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/keystores/products/"
	  
	ii) Navigate to "<Integration_Test>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import GooglePrediction certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from GooglePrediction, change it accordingly. (e.g. slsubgateway)
			   CERT_NAME is name of the certificate. (e.g. googleprediction)
	   
	iii) Goto new ESB 4.8.1 folder and place the downloaded certificate in "<ESB_Home>/repository/resources/security/"

	iv) Navigate to "<ESB_Home>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import GooglePrediction certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from GooglePrediction, change it accordingly. (e.g. slsubgateway)
			   CERT_NAME is name of the certificate. (e.g. googleprediction)
	
  8. Create and add a zip file of ESB 4.8.1 with applied keystores in step 7->(iii) and apply latest patches to the location: "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository"
  
  9. Navigate to url "https://developers.google.com/oauthplayground" and select autherize apis. You should select "Prediction API v1.6" API,
	 and select all options (tick all options under that API) and click "Authorize APIs". You have to login to your created test account in step 1=>(i)
	 Then it asks accept the permission for Google OAuth 2.0 play ground. accept and proceed. 

  10. Select "Exchange authorization code for tokens" to generate a new token. (Auth 2.0 play groung application step 2) 
      Then click the button "Exchange authorization code for tokens". It generates refresh token and access token for Prediction API. 
      Keep "Access Token" for future reference.	  

  11. Update the 'googleprediction.properties' file at the location "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config" as below.
   	 - apiUrl=Use the api url you got from step 1 (https://www.googleapis.com)
	 - accessToken=Modify the latest access token generated from Auth 2.0 play ground in step 10.
	 - project=Use the project number generated from step 1.
	 - analyzeFields=Change according to your filter requirement.
	 - storageDataLocation=[bucket name]/[filename] => Give your created bucket name and file name.[in step 1 => (vii) and (viii)] 
	 - trainModuleTimeout=Adjust according to size of the file, since the system takes time to train the module.
	 - modelId=Per each execution iteration this value should be changed.

	 [Note: Access token expires with in 3600 seconds. Make sure that your access token has not been expired before executing integration test.] 

  12. Navigate to "<Integration_Test_Home>/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
      	$ mvn clean install