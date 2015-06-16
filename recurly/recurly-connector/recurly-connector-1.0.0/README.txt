Product: Integration tests for WSO2 ESB Recurly connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

   Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.9.0-ALPHA

STEPS:

1. Download WSO2 ESB 4.9.0-ALPHA from official website.

2. Deploy relevant patches, if applicable and the ESB should be configured as below.
   Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
    <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

3. Follow the below mentioned steps to create a new Recurly account:

   i)   Navigate to the following URL and create an account in Recurly: https://app.recurly.com/signup
   ii)  Note down the Vanity URL provided in step 3-i which will used as the API URL.
   iii) Log In to the account, navigate to API Credentials page and obtain the API Key and save it for further use. 

4. Follow the below mentioned steps to add valid certificate to access Recurly API.

   i)   Extract the certificate from browser(Mozilla Firefox) by navigating to API URL that obtained in step 3-ii. 
   ii)  Go to new ESB 4.9.0-ALPHA folder and place the downloaded certificate into "{ESB_Connector_Home}/repository/resources/security/" and
        "{ESB_Connector_Home}/recurly/recurly-connector/recurly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folders.
   iii) Navigate to "{ESB_Connector_Home}/repository/resources/security/" using command prompt and execute the following command.

            keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

         This command will import Recurly certificate into keystore. 
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Recurly with the extension. (e.g. recurly.crt)
               CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Recurly)

   iv) Navigate to "{ESB_Connector_Home}/recurly/recurly-connector/recurly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

            keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" 

         This command will import recurly certificate in to keystore. Give "wso2carbon" as password.
         To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

         NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Recurly with the extension. (e.g. recurly.crt)
               CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Recurly)

5. Compress modified ESB as WSO2 ESB 4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

6. Update the property file recurly.properties found in {ESB_Connector_Home}/recurly/recurly-connector/recurly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:

   i)   apiUrl               -  API URL to which the service calls are made. e.g. https://virasoft.recurly.com.
   ii)  apiKey               -  Use the API key obtained in Step 3 - iii.
   iii) couponCode           -  A unique code for creating a coupon. This code may only contain the characters [a-z A-Z 0-9 @ - _ .]. Max of 50 characters.
   iv)  couponCodeOpt        -  A unique code for creating a optional coupon. This code may only contain the characters [a-z A-Z 0-9 @ - _ .]. Max of 50 characters.
   v)   accountCode          -  A unique code for creating a account. This code may only contain the characters [a-z 0-9 @ - _ .]. Max of 50 characters.
   vi)  accountCodeOpt       -  A unique code for creating a optional account. This code may only contain the characters [a-z 0-9 @ - _ .]. Max of 50 characters.
   vii) planCode             -  A unique code for creating a plan. This code may only contain the characters [a-z 0-9 @ - _ .]. Max of 50 characters.
   viii)planCodeOptional     -  A unique code for creating a optional plan. This code may only contain the characters [a-z 0-9 @ - _ .]. Max of 50 characters.
   ix)  discountPercentage   -  The discount percentage for coupons. Integer value between 0 to 100.
   x)   nameString           -  Use a valid string value for the coupon name.
   xi)  description          -  Use a valid string value for the coupon description.
   xii) perPage              -  Integer value indicating the number of results per page for pagination.

   Note: The property values of couponCode, couponCodeOpt, accountCode, accountCodeOpt, planCode, planCodeOptional should be changed to unique different values for each integration execution.

7. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install