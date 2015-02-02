Product: Integration tests for WSO2 Flickr connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform:

 - Linux 3.11.0-19-generic (Ubuntu 13.10)
 - WSO2 ESB 4.8.1

STEPS:

1. Add the WSO2 ESB 4.8.1 to the {basedir}/test folder.
    If you want to use another location, please change it accordigly in the pom.xml as follows.

          <carbon.zip>
            ${basedir}/../test/wso2esb-${esb.version}.zip
          </carbon.zip>


2. Make sure the flickr test suite is enabled (as given below) and all other test suites are commented in the "testng.xml" file.

	<test name="Flickr-Connector-Test" preserve-order="true" verbose="2">
            <packages>
                <package name="org.wso2.carbon.connector.integration.test.flickr"/>
            </packages>
    </test>


3. Update the flickr.properties file with your details if you have any or you can use the default account details as it is.

    consumerKey=00a49886c96cd978d1c513cb2d33742b
    consumerKeySecret=f20cca596738e959
    accessToken=72157642842074863-5faf0186518885ab
    accessTokenSecret=70cd37fb502cb6fd

4. Navigate to "${basedir}" and run the following command.
     $ mvn clean install

Account details used in the tests are as follows.

Developer account :
    username:nalin.wso2@yahoo.com
    password:wso2Carbon

Test client account:
    username:nalin.wso2client@yahoo.com
    password:wso2Carbon

