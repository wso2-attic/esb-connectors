Product: Integration tests for WSO2 ESB Eloqua connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- Mac OSx 10.9
- WSO2 ESB 4.9.0-ALPHA
- Java 1.7

STEPS:

1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.

2. Follow the below mentioned steps to add valid certificate to access Eloqua API over https.

	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://login.eloqua.com/'
	ii)  Go to new ESB 4.9.0-ALPHA folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and
		 "{Eloqua_Connector_Home}/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.
	
			keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"
				
		 	This command will import Eloqua certificate into keystore. 
		 	To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 	NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Eloqua with the extension. (e.g. eloqua.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Eloqua)

	iv) Navigate to "{Eloqua_Connector_Home}/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.
		
			keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 
				
			This command will import eloqua certificate in to keystore. Give "wso2carbon" as password.
			To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
			NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Eloqua with the extension. (e.g. eloqua.crt)
				CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Eloqua)

3. ESB Axis2 configurations

    Ensure that the following Axis2 configurations are added and enabled in the <ESB_HOME>\repository\conf\axis2\axis2.xml file.
    	Required message formatters:
        	<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		Required message builders
        	<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>


5. Compress the modified ESB zip and copy that in to location "{ESB_Connector_Home}/repository/".

6. Make sure that eloquaStandardAPI is specified as a module in ESB_Connector_Parent pom.
    <module>eloquaStandardAPI/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon.connector</module>

4. Modify the proxy file in following location "{Eloqua_Connector_Home}/eloqua-connector/eloqua-connector-1.0.0/org
.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/proxies/eloqua/"

5. Copy request files to following location "{Eloqua_Connector_Home}/eloqua-connector/eloqua-connector-1.0.0/org.wso2
.carbon.connector/src/test/resources/artifacts/ESB/config/restRequests/eloqua/" and modify corresponding values in "eloqua.properties" at {Eloqua_Connector_Home}/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/connector/config/

6. Edit the "eloqua.properties" at {Eloqua_Connector_Home}/eloqua-connector/eloqua-connector-1.0.0/org.wso2.carbon
.connector/src/test/resources/artifacts/connector/config/ using valid and relevant data. Parameters to be changed are mentioned below.

	- siteName: the company name that the user use to login.
	- username: the username that the user use to login.
	- password: the password that the user use to login.

7. Navigate to "{ESB_Connector_Home}/" and run the following command.
    $ mvn clean install