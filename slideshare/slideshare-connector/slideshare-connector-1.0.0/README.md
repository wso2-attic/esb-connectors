Integration tests for WSO2 ESB slideshare connector

Prerequisites:
- Maven 3.x
- Java 1.6 or above

Tested Platform:
- Ubunthu 14.04
- WSO2 ESB 4.9.0-ALPHA
- Java 1.7

STEPS:

    1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/


    2.  Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following
        file - "slideshare/src/test/resources/testng.xml"

         <test name="slideShare-Connector-Test" preserve-order="true" verbose="2>
              <packages>
                        <package name="org.wso2.carbon.connector.integration.test.slideshare"/>
              </packages>
         </test>

    3. Follow the below mentioned steps to add valid certificate to access slideshare API over https.

          i)   Extract the certificate by navigating to 'https://www.slideshare.net/login/'
          ii)  Place the downloaded certificate into "<SLIDESHARE_CONNECTOR_HOME>/slideshare-connector/slideshare-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products" folder.
          iii) Navigate to "<SLIDESHARE_CONNECTOR_HOME>/slideshare-connector/slideshare-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

                      keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME"

               This command will import slideshare certificate in to keystore. Give "wso2carbon" as password.
               To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.


          iv) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

		       This command will import slideshare certificate into keystore.
		       To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

		   NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from slideshare with the extension. (e.g. slideshare.crt)
                              CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. slideshare).

    3.  Copy proxy files to following location:
        "<SLIDESHARE_CONNECTOR_HOME>/src/test/resources/artifacts/ESB/config/proxies/slideshare"

    4.  Copy request files to following:
        "<SLIDESHARE_CONNECTOR_HOME>/src/test/resources/artifacts/ESB/config/restRequests/slideshare/"


    5.  Following data set can be used for the first testsuite run.
         proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/slideshare/
         requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/slideshare/

    6.  For testing
        Account Details:
        username: tvanii@gmail.com
        password: SlideShare*


    7. Make sure that the slideshare connector is set as a module in esb-connectors parent pom.
      <module>slideshare/slideshare-connector/slideshare-connector-1.0.0/org.wso2.carbon.connector</module>

    8. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
      $ mvn clean install