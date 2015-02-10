Nimble-Connector
================
Product: Integration tests for WSO2 ESB Nimble connector
Prerequisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform:

- Mac OS X V-10.9.5
- WSO2 ESB 4.8.1
- Java 1.7

STEPS:

1.Make sure the ESB 4.8.1 zip file with latest patches available at:
  “nimble/repository/”.
  - import the gdroot-g2_cross CERTIFICATE to your ESB client’s keystore.
  	- import your gdroot-g2_cross certificates into wso2esb client’s keystore as follows:
  		Go to https://certs.godaddy.com/repository in your browser and click gdroot-g2_cross.crt
  		Download and put the certificate at
  		<ESB_HOME>/repository/resources/security/
  		Navigate to <ESB_HOME>/repository/resources/security/ folder on terminal and type the following command.
  keytool -importcert -file <CERTIFICATE_FILE_NAME WITH EXTENSION> -keystore client-truststore.jks -alias “<CERTIFICATE_NAME>”
  and give wso2carbon as password.

2.Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file -
  “nimble/src/test/resources/testng.xml"

   <test name="nimble-Connector-Test" preserve-order="true" verbose="2>
   <packages>
   <package name="org.wso2.carbon.connector.integration.test.nimble"/>
   </packages>
   </test>

3.Copy proxy files to following location:
    "nimble/src/test/resources/artifacts/ESB/config/proxies/nimble"

4.Copy request files to following:
    "nimble/src/test/resources/artifacts/ESB/config/restRequests/nimble/"

5.Edit the request files at 4 using valid and relevant data:
  Parameters to be changed are mentioned below.
   - login https://www.nimble.com/login/ - you may use the dummy Account details below
   - request access token - To get the access token you have to follow the steps in this documentation manually http://nimble.readthedocs.org/en/latest/obtaining_key/ then use the values obtained for refresh token,client id, client secret.

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

7. Navigate to "nimble/” and run the following command.
    $ mvn clean install
