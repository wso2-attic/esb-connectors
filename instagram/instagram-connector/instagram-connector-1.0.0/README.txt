Product: Integration tests for WSO2 ESB Instagram Connector 
 
 
Pre-requisites: 
 
 - Maven 3.x 
 - Java 1.6 or above 
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. However if its     failed download following project and compile using mvn clean install command to update your local repository. 
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base 
 
 
Tested Platform: 
 
 - UBUNTU 13.04 
 - WSO2 ESB 4.8.1 
 
 
STEPS: 
 
 
 1. Make sure the ESB 4.8.1 zip at "{PATH_TO_SOURCE_BUNDLE}/instagram-connector/instagram-connector-1.0.0/org.wso2.carbon.connector/repository/"
  
 
 2. Create a Instagram account and derive the access token: 
        i)  To create an Instagram account, please register using the Instagram app on iPhone or Android. 
        ii) Derive the access token by following the instructions at "http://instagram.com/developer/authentication/". 
 
 3. import the certificate to the esb client keystore as follows, 
    i) Go tohttps://api.instagram.com/oauth/access_token?                     client_id=CLIENT_ID&client_secret=CLIENT_SECRET&grant_type=authorization_code&redirect_uri=REDIRECT-URI&code=CODE  in your browser, and then    click the HTTPS trust icon on the address bar (e.g., the padlock next to the URL in Firefox). 
     
    ii) View the certificate details (the steps vary by browser) and then export the trust certificate to the file system. 
 
    iii) Use the ESB Management Console or the following command to import that certificate into the ESB client keystore. 
    keytool -importcert -file <certificate file> -keystore <ESB>/repository/resources/security/client-truststore.jks -alias     "InstagramTrustCertImport" 
 
    iv) Restart the server and deploy the Instagram configuration. 
 
    v) Also import certificate into the keystores files(client-truststore.jks & wso2carbon.jks) in the test module.   
        (src/test/resources/keystores/products and src/test/resources/keystores/stratos) 
 
 
 4. Update the Instagram properties file at location "{PATH_TO_SOURCE_BUNDLE}/instagram-connector/instagram-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below. 
    
    Following properties should be changed by using the details obtained from the created app in the developer account of Instagram. 
        accessToken - Use the access token you got from step 2. 
       
  
 5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/instagram-connector/instagram-connector-1.0.0/org.wso2.carbon.connector/" and run the following command. 
      $ mvn clean install

-------------------------------------------------
credential of test account
sandbox:	login		: wso2esbconnectorins	
		password	: connector12345

email	:	login		: wso2instagram@gmail.com	
		password	: wso2instagram123456
-------------------------------------------------
