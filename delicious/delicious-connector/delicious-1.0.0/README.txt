Product: Integration tests for WSO2 ESB Delicious connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above


Tested Platform: 

 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7


STEPS:
 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

 2. This ESB should be configured as below;
  Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

   <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
   
   <messageFormatter contentType="application/x-www-form-urlencoded" class="org.apache.axis2.transport.http.XFormURLEncodedFormatter"/>
   
   <messageFormatter contentType="text/javascript" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>  
   
   <messageFormatter contentType="application/octet-stream" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/> 
   
   <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   <messageBuilder contentType="application/x-www-form-urlencoded" class="org.apache.synapse.commons.builders.XFormURLEncodedBuilder"/>
   
   <messageBuilder contentType="text/javascript" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   <messageBuilder contentType="application/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   

  4. Update the Delicious properties file at location "{ESB_Connector_Home}/delicious-connector/delicious-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

    i) Basic
	Use your delicious userneme and password olny.

  5. Make sure that the delicious connector is set as a module in esb-connectors parent pom.
           <module>delicious/delicious-connector/delicious-connector-1.0.0/org.wso2.carbon.connector</module>

  6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
           $ mvn clean install


 NOTE : Following Delicious account, can be used for run the integration tests.
      username: wso2delicious
      password: !2qwasZX
      Client Id: b7a9418896428755bda9274bb53a27be
      Client Secret: d3cba665ddf8e5c3a1490faf0787bf64




