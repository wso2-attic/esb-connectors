Product: Integration tests for WSO2 ESB Billiving connector

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

3. Follow the below mentioned steps to create a new Billiving account:

    i)   Navigate to the following url and create an account in Billiving: https://www.billiving.com/Signup and activate the account.
    ii)  Login to Billiving account and navigate to 'Settings' and click "API Integration", retrieve the Authentication Token and save it for further use.

4. Follow the below mentioned steps to add valid certificate to access Billiving API over https.

    i)   Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://billiving.com/' 
    ii)  Go to new ESB 4.8.1 folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and 
         "{Billiving_Connector_Home}/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
    iii) Navigate to "{Billiving_Connector_Home}/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Billiving certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Billiving with the extension. (e.g. billiving.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Billiving).

5. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Billiving_Connector_Home}/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/repository/".

6. Update the property file billiving.properties found in {Billiving_Connector_Home}/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:

    i)    apiUrl              -  API endpoint to which the service calls are made. e.g. https://www.billiving.com.
    ii)   accessToken         -  Use the Access token obtained in Step 3 - ii.
    iii)  internalNotes       -  Use Any string value.
    iv)   clientEmail         -  Use a valid email address.
    v)    clientEmailoptional -  Use a valid email address.
    vi)   telephoneNumber     -  Use a Valid phone number.
    vii)  ItemId              -  Use Any String value. 
    viii) ItemDescription     -  Use Any String value.
    ix)   status              -  Use a Valid status id for an invoice.
    x)    shipping            -  Use Any decimal value.

    Note: Use different values for clientEmail and clientEmailoptional for each execution.

7. Navigate to "{Billiving_Connector_Home}/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install