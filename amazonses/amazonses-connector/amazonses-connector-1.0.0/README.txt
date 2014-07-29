Product: Integration tests for WSO2 ESB AMAZONSES connector

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
 
 1. Follow the below mentioned steps for adding valid certificate to access AMAZONSES API over https

	i) Extract the certificate from browser by navigating to 'https://email.us-west-2.amazonaws.com' and place the certificate file in following location.
	   "{AMAZONSES_CONNECTOR_HOME}/amazonses-connector/amazonses-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	  
	ii) Navigate to "{AMAZONSES_CONNECTOR_HOME}/amazonses-connector/amazonses-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import AmazonSES certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from AMAZONSES, change it accordingly. (e.g. -.amazonses.com)
			   CERT_NAME is name of the certificate. (e.g. amazonses)
	   
	iii) Go to new ESB 4.8.1 folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import AMAZONSES certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from AmazonSES, change it accordingly. (e.g. -.amazonses.com)
		       CERT_NAME is name of the certificate. (e.g. amazonses)
			   
 2. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{AMAZONSES_CONNECTOR_HOME}/amazonses-connector/amazonses-connector-1.0.0/org.wso2.carbon.connector/repository/".

 3. Create a fresh account in Amazon AWS and Log on to https://aws.amazon.com/ses/ in the web browser.
	i) 	 	On the hompage, under App Service, choose SES.
	ii)  	Click on the dropdown link on the top right tab (which has your account name) on your Amazon SES page and go to 'Security Credentials'.
	iii) 	Click on 'Access Keys', create a new Access Key and save you AWSAccessKeyID along with the SecretKey.
	iv) 	On SES homepage, go to 'Email Addresses' link and add a few valid email addresses to be used while testing (Email addresses should be verified to be used).

 4. Update the amazonses properties file at location "{AMAZONSES_CONNECTOR_HOME}/amazonses-connector/amazonses-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl - Use the API URL as "https://email.us-west-2.amazonaws.com".
	ii) 	accessKeyId, AWSAccessKeyId - Use the AWSAccessKeyId you got in step 3.
	iii) 	secretAccessKey - Use the SecretKey you got in step 3.
	iv)		identity,emailAddress,deleteIdentity,source - (Single valued) Use email addresses which you added to SES account in step 3 and verified. (Multiple email addresses need to be comma seperated)
	v) 		toAddresses,ccAddresses,bccAddresses,replyToAddresses,destinations,identities - (Multi valued) Use email addresses which you added to SES account in step 3 and verified. (Multiple email addresses need to be comma seperated)
	vi)     snsTopic -  Use a valid SNS topic name (Amazon Resource Name).
	vii) 	messageBody,messageSubject,messageBodyMandatory,messageSubjectMandatory - Use valid text content as desired.
	viii) 	Leave the remaining values as it is
		
 6. Navigate to "{AMAZONSES_CONNECTOR_HOME}/amazonses-connector/amazonses-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install 

 NOTE :  
    i)  Maximum emails that can be sent via Amazon SES is 200/day .
    ii) Following Amazon SES credentials, can be used to run the integration tests.
 	    secretAccessKey=kBTcya0uHKs4dT/wTJFin20WxSeQlObOhhTATpaX
		accessKeyId=AKIAIMB25APSJTHD53EQ