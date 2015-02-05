Integration tests for WSO2 ESB slideshare connector

Prerequisites:
- Maven 3.x
- Java 1.6 or above

Tested Platform:
- Ubunthu 14.4
- WSO2 ESB 4.8.1
- Java 1.7

STEPS:

    1.  Make sure the ESB 4.8.1 zip file with latest patches available at:
         “slideshare/repository/”.

    2.  Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following
        file - "slideshare/src/test/resources/testng.xml"

         <test name="slideShare-Connector-Test" preserve-order="true" verbose="2>
              <packages>
                        <package name="org.wso2.carbon.connector.integration.test.slideshare"/>
              </packages>
         </test>

    3.  Copy proxy files to following location:
        "slideshare/src/test/resources/artifacts/ESB/config/proxies/slideshare"

    4.  Copy request files to following:
        "slideshare/src/test/resources/artifacts/ESB/config/restRequests/slideshare/"


    5.  Following data set can be used for the first testsuite run.
         proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/slideshare/
         requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/slideshare/

    6.  For testing
        Account Details:
        username: tvanii@gmail.com
        password: SlideShare*

    6.  Navigate to "slideshare” and run the following command.

    7.  $ mvn clean install
