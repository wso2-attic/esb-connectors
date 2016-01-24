Product: Integration tests for WSO2 ESB Stripe connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above
- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
                 https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

- Mac OSx 10.9
- WSO2 ESB wso2esb-4.8.1
- Java 1.7

STEPS:

1. Make sure the wso2esb-4.9.0-ALPHA.zip file at "<ESB_CONNECTORS_HOME>/repository/".
        Make sure your axis2.xml contains following entries.

            <messageFormatter contentType="multipart/form-data" class="org.apache.axis2.transport.http.MultipartFormDataFormatter"/>

            <messageBuilder contentType="multipart/form-data" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

        Be sure to import the Stripe certificate to your ESB client keystore.
            You can follow the following steps to import your Stripe certificates into wso2esb client’s keystore as follows:
               1. Go to https://stripe.com/docs/connect/oauth in your browser, and then click the HTTPS trust icon on the address bar (e.g., the padlock next to the URL in Firefox).
               2. View the certificate details (the steps vary by browser) and then export the trust certificate to the file system.
               3. Use the ESB Management Console or the following command to import that certificate into the ESB client keystore.
                 keytool -importcert -file <certificate file> -keystore <ESB>/repository/resources/security/client-truststore.jks -alias "StripeTrustCertImport"
               4. Restart the server and deploy the Stripe configuration.

2. Edit the "stripe.properties" at stripe/src/test/resources/artifacts/connector/config/ using valid and relevant data. Parameters to be changed are mentioned below.

	- proxyDirectoryRelativePath: relative path of the rest request files folder from target.
	- requestDirectoryRelativePath: relative path of proxy folder from target.
	- propertiesFilePath: relative path of properties file from target.
    - clientSecret: to get the access token for a particular client_secret.
	- refreshToken: refresh token to get the access token.
	- apiUrl: API URL.
		
3. Following data set can be used for the first testsuite run.

		proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/stripe/
		requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/stripe/
		propertiesFilePath=/../src/test/resources/artifacts/ESB/connector/config/
 		refreshToken=rt_5KeYCAqc76HbrUx5RVfJEGt3xCRHZrXZOSoXGNna57DC4JNf
		clientSecret=sk_test_Gd3JGidPIzfPkMOC8ZGnPEdf
        apiUrl=https://api.stripe.com
        apiVersion=v1

4. Required to change on test
	change the relevant data in the corresponding text file(Text files in the rest requests folder)

5. Make sure that the stripe connector is set as a module in esb-connectors parent pom.
        <module>stripe</module>

6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
          $ mvn clean install