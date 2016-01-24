Product: Integration tests for WSO2 Flickr connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform:

 - Linux 3.11.0-19-generic (Ubuntu 13.10), Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

STEPS:

1. Add the WSO2 ESB 4.9.0-ALPHA to the {ESB_Connector_Home}/repository/ folder.
    If you want to use another location, please change it accordingly in the pom.xml as follows.

          <carbon.zip>
            ${basedir}/../test/wso2esb-${esb.version}.zip
          </carbon.zip>

2. Update the flickr.properties file with your details if you have any or you can use the default account details as it is.

    consumerKey=00a49886c96cd978d1c513cb2d33742b
    consumerKeySecret=f20cca596738e959
    accessToken=72157642842074863-5faf0186518885ab
    accessTokenSecret=70cd37fb502cb6fd

3.Make sure that the flickr connector is set as a module in esb-connectors parent pom.
     <module>flickr/flickr-connector/flickr-connector-1.0.0/org.wso2.carbon.connector</module>

4. Navigate to "{ESB_Connector_Home}/" and run the following command.
     $ mvn clean install

Account details used in the tests are as follows.

Developer account :
    username:nalin.wso2@yahoo.com
    password:wso2Carbon

Test client account:
    username:nalin.wso2client@yahoo.com
    password:wso2Carbon