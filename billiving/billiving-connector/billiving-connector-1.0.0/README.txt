Product: Integration tests for WSO2 ESB Billiving connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

   Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - Mac OSx 10.9
    - WSO2 ESB 4.9.0-ALPHA
 
STEPS:

1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

2. Follow the below mentioned steps to create a new Billiving account:

    i)   Navigate to the following url and create an account in Billiving: https://www.billiving.com/Signup and activate the account.
    ii)  Login to Billiving account and navigate to 'Settings' and click "API Integration", retrieve the Authentication Token and save it for further use.

3. Follow the below mentioned steps to add valid certificate to access Billiving API over https.

    i)   Extract the certificate from browser(Mozilla Firefox) by navigating to 'https://www.billiving.com/' 
    ii)  Place the downloaded certificate into "<BILLIVING_CONNECTOR_HOME>/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folder.
    iii) Navigate to "<BILLIVING_CONNECTOR_HOME>/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

                keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import Billiving certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Billiving with the extension. (e.g. billiving.crt)
                CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Billiving).

4. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".

5. Update the property file billiving.properties found in <BILLIVING_CONNECTOR_HOME>/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:

    i)    apiUrl              -  API endpoint to which the service calls are made. e.g. https://www.billiving.com.
    ii)   accessToken         -  Use the Access token obtained in Step 2 - ii.
    iii)  internalNotes       -  Use Any string value.
    iv)   clientEmail         -  Use a valid email address.
    v)    clientEmailoptional -  Use a valid email address.
    vi)   telephoneNumber     -  Use a Valid phone number.
    vii)  ItemId              -  Use Any String value. 
    viii) ItemDescription     -  Use Any String value.
    ix)   status              -  Use a Valid status id for an invoice.
    x)    shipping            -  Use Any decimal value.

    Note: Use different values for clientEmail and clientEmailoptional for each execution.

5. Make sure that the billiving connector is set as a module in esb-connectors parent pom.
      <module>billiving/billiving-connector/billiving-connector-1.0.0/org.wso2.carbon.connector</module>

6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
      $ mvn clean install