YouTube
=======
Product: Integration tests for WSO2 ESB YouTube connector

Prerequisites:

    Maven 3.x
    Java 1.6 or above

Tested Platform:

    ubuntu 14.04
    WSO2 ESB 4.8.1
    Java 1.7

STEPS:
1. Make sure the EsB 4.8.1 zip file with latest patches at: "YouTube/YouTube-connector/YouTube-1.0.0/repository"

2. This ESB should be configured as below; In Axis configurations (ESB/repository/conf/axis2.xml).

Message Formatters:
<messageFormatter contentType="application/octet-stream"
                        class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
<messageFormatter contentType="video/*"
                        class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

Message Builders:
<messageBuilder contentType="application/octet-stream"
                        class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
<messageBuilder contentType="video/*"
                        class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

3. Make sure the YouTube test suite is enabled (as given below) in the following 
   file - "YouTube/YouTube-connector/YouTube-1.0.0/src/test/resources/testng.xml"  
        <test name="YouTube-Connector-Test" preserve-order="true" verbose="2">
            <packages>
                <package name="org.wso2.carbon.connector.integration.test.YouTube"/>
            </packages>
        </test>

4.  Update the connector properties 
    file "YouTube/YouTube-connector/YouTube-1.0.0/ src/test/resources/artifacts/ESB/connector/config/YouTube.properties"
   
      i)  clientId and clientSecret - client id and client secret of registered application.
   
      ii) refreshToken
      
5. Copy proxy files to location "YouTube/YouTube-connector/YouTube-1.0.0/src/test/resources/artifacts/ESB/config/proxies/YouTube"

6. Copy request files to location "YouTube/YouTube-connector/YouTube-1.0.0/src/test/resources/artifacts/ESB/config/restRequests/YouTube" 

7. Navigate to "YouTube/YouTube-connector/YouTube-1.0.0" and run the following command.
        $ mvn clean install

Account Details: 
         username: wso2connector.youtub@gmail.com
         password: esbconnector


