Product: Integration tests for WSO2 ESB SoundCloud Connector


Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. However if its 	failed download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base


Tested Platform:

 - UBUNTU 13.04
 - WSO2 ESB 4.8.1


STEPS:


 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/soundcloud-connector/soundcloud-connector-1.0.0/org.wso2.carbon.connector/repository/"
 

 2. Create a SoundCloud account and derive the access token:
        i)  Using the URL "https://soundcloud.com/" create a SoundCloud account.
        ii) Derive the access token by following the instructions at "http://developers.soundcloud.com/docs/api/reference#token".


 3. import the certificate to the esb client keystore as follows,
	i) Go to https://api.soundcloud.com/me.json?oauth_token=<your_oauth_token> in your browser, and then click the HTTPS trust icon on the 		address bar (e.g., the padlock next to the URL in Firefox).
	
	ii) View the certificate details (the steps vary by browser) and then export the trust certificate to the file system.

	iii) Use the ESB Management Console or the following command to import that certificate into the ESB client keystore.
	keytool -importcert -file <certificate file> -keystore <ESB>/repository/resources/security/client-truststore.jks -alias 	"SoundCloudTrustCertImport"

	iv) Restart the server and deploy the SoundCloud configuration.

	v) Also import certificate into the keystores files(client-truststore.jks & wso2carbon.jks) in the test module.  
        (src/test/resources/keystores/products and src/test/resources/keystores/stratos)


 4. Update the SoundCloud properties file at location "{PATH_TO_SOURCE_BUNDLE}/soundcloud-connector/soundcloud-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
	Following properties should be changed by using the details obtained from the created app in the developer account of SoundCloud.
	    accessToken - Use the access token you got from step 2.
	    consumerKey - Client ID of the created app in SoundCloud.
	    consumerKeySecret - Client secret of the created app in SoundCloud.
 
 5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/soundcloud-connector/soundcloud-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install

NOTE : Following SoundCloud account, can be used for run the integration tests.
    Username : wso2soundcloud@gmail.com
    Password : soundcloud1234
    Client Key: 8640b475697d4c379fa6f62c4dbee0af
    Access token: 1-87105-103067839-90557e733573b1
    Redirect URI: https://www.example.com/soundcloud
    
    
