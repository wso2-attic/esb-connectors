Product: Integration tests for WSO2 ESB Delicious connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above


Tested Platform: 

 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure WSO2 ESB 4.8.1 to the {basedir}/test folder.
    If you want to use another location, please change it accordigly in the pom.xml as follows.

          <carbon.zip>
            ${basedir}/../test/wso2esb-${esb.version}.zip
          </carbon.zip>

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
   

  4. Update the Delicious properties file at location "{PATH_TO_SOURCE_BUNDLE}/delicious-connector/delicious-connector-1.0.0/src/test/resources/artifacts/ESB/connector/config" as below.
   
    i) Oauth2
	Client Id,Client Secret, User name, Password  
        
        or

    ii) Basic
	Use your delicious userneme and password olny.

    
 5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/delicious-connector/delicious-connector-1.0.0/" and run the following command.
      $ mvn clean install


 NOTE : Following Delicious account, can be used for run the integration tests.
      username: wso2delicious
      password: !2qwasZX
      Client Id: b7a9418896428755bda9274bb53a27be
      Client Secret: d3cba665ddf8e5c3a1490faf0787bf64




