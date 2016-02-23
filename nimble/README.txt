Nimble-Connector
================
Product: Integration tests for WSO2 ESB Nimble connector
Prerequisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform:

- Mac OS X V-10.9.5
- WSO2 ESB 4.9.0
- Java 1.7

STEPS:

1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.

2.Follow the below mentioned steps to add valid certificate to access Nimble API over https.

  - import the gdroot-g2_cross CERTIFICATE to your ESB client’s keystore.
  	- import your gdroot-g2_cross certificates into wso2esb client’s keystore as follows:
  		Go to https://certs.godaddy.com/repository in your browser and click gdroot-g2_cross.crt
  		Download and put the certificate at
  		<ESB_HOME>/repository/resources/security/
  		Navigate to <ESB_HOME>/repository/resources/security/ folder on terminal and type the following command.
  keytool -importcert -file <CERTIFICATE_FILE_NAME WITH EXTENSION> -keystore client-truststore.jks -alias “<CERTIFICATE_NAME>”
  and give wso2carbon as password.

3. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

4.  Follow the below mentioned steps to create a new Nimble account:
     - login https://www.nimble.com/login/ - you may use the dummy Account details below
     - request access token - To get the access token you have to follow the steps in this documentation manually http://nimble.readthedocs.org/en/latest/obtaining_key/ then use the values obtained for refresh token,client id, client secret.
5.Update the property file onstantcontact.properties found in {Connector_Home}/nimble/nimble-connector/nimble-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows::

  i) apiUrl=https://api.nimble.com/api/v1
  ii) refreshToken Use the refreshToken  obtained in Step 4
  iii) clientId Use the clientId obtained in Step 4
  iv) clientSecret Use the clientSecret obtained in Step 4
  v) grantType Use the redirectUri obtained in Step 4
  vi) redirectUri Use the grantType  obtained in Step 4
  vii) contactId create a contact and retrive it's id from app
  viii) relatedTo give the above created id
  ix) noteId create a contact and retrive it's id from app
  x ) name  Use a unique and a valid string value
  xi) fieldId create a field and retrive it's id from app

6.Following data set can be used for the first test-suite to run.

    Proxy Directory Relative Path=/../src/test/resources/artifacts/ESB/config/proxies/nimble/
    Request Directory Relative Path = /../src/test/resources/artifacts/ESB/config/restRequests/nimble/

    "refreshToken":"e271559f-8a3a-4440-b1f6-cf35aab15caa",
    "clientId":"1h6nd4niu1w69minbc97la9pjjb5oa1bdskud5",
    "clientSecret":"uqwh5vimq2majy8i0g",
    "redirectUri":"http://elilsivanesan.blogspot.com/"

    Account Details:
    username: testnimble33@gmail.com
    password: 0777498522Me!

7. Make sure that nimble is specified as a module in ESBConnector_Parent pom.
    <module>nimble/nimble-connector/nimble-connector-1.0.0/org.wso2.carbon.connector</module>

8. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install

      note
      Trail account allows to create 3 groups and 3 fields So the integration test will run only 3 times.
