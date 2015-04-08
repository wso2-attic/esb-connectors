Product: Integration tests for WSO2 ESB Podio connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

   Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1
 
STEPS:

1. Download ESB 4.8.1 from official website.

2. Deploy relevant patches, if applicable.
   Please make sure that Axis configurations are enabled (\repository\conf\axis2\axis2.xml) according to the file type to be uploaded.

3. Follow the below mentioned steps to create a new Podio account:

    i)   Navigate to the following url and create an account in Podio: https://podio.com/signup and activate the account.
    ii)  Login to Podio account and navigate to 'API Keys' tab in "Account Settings", retrieve the Client ID and Client Secret and save it for further use.
    iii) Generate the Access Token as mentioned in the following URL. https://developers.podio.com/authentication

4. Follow the below mentioned steps to add valid certificate to access Podio API over https.

    i)   Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://podio.com/' 
    ii)  Go to new ESB 4.8.1 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
         "{Podio_Connector_Home}/podio-connector/podio-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import Podio certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Podio with the extension. (e.g. podio.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Podio)

    iv) Navigate to "{Podio_Connector_Home}/podio-connector/podio-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Podio certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Podio with the extension. (e.g. podio.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Podio).

5. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Podio_Connector_Home}/podio-connector/podio-connector-1.0.0/org.wso2.carbon.connector/repository/".

6. Add the file which is used for upload file method to the following location and update the value of the property iii corresponding to the added file.
    Location: "{Podio_Connector_Home}/podio-connector/podio-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/podio/"

7. Update the property file podio.properties found in {Podio_Connector_Home}/podio-connector/podio-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:

    i)  apiUrl           -  API endpoint to which the service calls are made. e.g. https://api.podio.com.
    ii) accessToken      -  Use the Access token obtained in Step 3 - iii.
    iii)uploadSourcePath -  File name with the extention which is added to resources folder in Step 6.
    iv) userId           -  User ID of the account owner.
    v)  text             -  Use a string value for task text.
    vi) remindDelta      -  Use a positive integer value lower than 1440 for the minutes to use in reminder.
    vii)timeOut          -  Time out value for waiting since the podio API limit the continuous endpoint calls (recommended value is 5000)

8. Navigate to "{Podio_Connector_Home}/podio-connector/podio-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
