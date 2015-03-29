Product: Integration tests for WSO2 ESB Eloqua connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

- UBUNTU 14.04
- WSO2 ESB wso2esb-4.8.1
- Java 1.6

STEPS:

 1. Make sure the ESB 4.8.1 zip file available at "{ELOQUA_CONNECTOR_HOME}/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon.connector/repository/" and import the Eloqua API certificate to your ESB client keystore.

 2. Create a App and get the clientId, clientSecret callback url and refreshToken.

     i)Register as a provider-To register as a Provider, log in to your Eloqua instance, and navigate to the Setup menu, then choose AppCloud Developer.
       Then, click Create a Provider, which brings you to the AppCloud Developer “Create New Provider” page.

     ii)Fill out your company’s information, click Save, and you’re all set up as a provider.

     iii)Create the app with the required details.

     iv)Get the code from the following call-https://login.eloqua.com/auth/oauth2/authorize?response_type=code&client_id={clientId}&redirect_
         uri={redirectUri}&scope=full&state=xyz

      v)Get the refreshToken from the following call, The request must also authenticate your app using HTTP basic authentication using your App’s client identifier as the username and your App’s client secret as the password.
       The format is: client_id:client_secret
       Encode the string with base-64 encoding, and you can pass it as an authentication header.

        POST https://login.eloqua.com/auth/oauth2/token
        Authorization: Basic XXXXXXXXXXX
        Content-Type   application/json
        {
           "grant_type":"authorization_code",
           "code":"code",
           "redirect_uri":"redirectUri"
        }

        for more details see, "http://docs.oracle.com/cloud/latest/marketingcs_gs/OMCBB/index.html#C_Tutorials/authenticate-using-oauth.htm%3FTocPath%3DTutorials%7C_____2"
	 
 3. Update the eloqua properties file at location "{ELOQUA_CONNECTOR_HOME}/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
 
      - refreshToken -  Use the refreshToken you got from step 2.

      - clientId - Use the clientId you got from step 2.
	
	  - clientSecret - Use the clientSecret you got from step 2.
	
      - redirectUri - Use the redirectUri you got from step 2.

      - apiUrl - The api url of Eloqua.

      - siteName - The site name of the user.

      - username - The username of the user.

      - password - The password of the user.

      - apiVersion - The version of the API.

      - scope - The scope of the token.
   
      -Get the values for contactFieldId, contactFilterId, contactListId, contactSegmentId, emailGroupId, accountFieldId, accountListId, activityTypeId and customObjectId

 4. Navigate to "{ELOQUA_CONNECTOR_HOME}/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install


