Product: Integration tests for WSO2 ESB Instagram Connector 


Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
            https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1
 
 
Tested Platform: 
 
 - Mac OSx 10.9
 - WSO2 ESB 4.9.0-BETA
 
 
STEPS: 
 
 
 1. Download the ESB 4.9.0-ALPHA.zip
  
 
 2. Create a Instagram account and derive the access token: 
        i)  To create an Instagram account, please register using the Instagram app on iPhone or Android. 
        ii) Derive the access token by following the instructions at "http://instagram.com/developer/authentication/". 
 
 3. import the certificate to the esb client keystore as follows, 
    i) Go tohttps://api.instagram.com/oauth/access_token?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&grant_type=authorization_code&redirect_uri=REDIRECT-URI&code=CODE  in your browser, and then    click the HTTPS trust icon on the address bar (e.g., the padlock next to the URL in Firefox).
     
    ii) View the certificate details (the steps vary by browser) and then export the trust certificate to the file system. 
 
    iii) Use the ESB Management Console or the following command to import that certificate into the ESB client keystore. 
    keytool -importcert -file <certificate file> -keystore <ESB>/repository/resources/security/client-truststore.jks -alias "InstagramTrustCertImport"
 
    iv) Restart the server and deploy the Instagram configuration. 
 
    v) Also import certificate into the keystores files(client-truststore.jks & wso2carbon.jks) in the test module.   
        (src/test/resources/keystores/products and src/test/resources/keystores/stratos) 

 4. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".
 
 5. Update the Instagram properties file at location "{PATH_TO_SOURCE_BUNDLE}/instagram-connector/instagram-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
    
    Following properties should be changed by using the details obtained from the created app in the developer account of Instagram. 
        accessToken - Use the access token you got from step 2. 
       
  
  6. Make sure that the marketo connector is set as a module in esb-connectors parent pom.
         <module>instagram/instagram-connector/instagram-connector-1.0.0</module>


  7. Navigate to "{ESB_CONNECTORS_HOME}/" and run the following command.
       $ mvn clean install

-------------------------------------------------
credential of test account
sandbox:	login		: wso2esbconnectorins	
		password	: connector12345

email	:	login		: wso2instagram@gmail.com	
		password	: wso2instagram123456
-------------------------------------------------
