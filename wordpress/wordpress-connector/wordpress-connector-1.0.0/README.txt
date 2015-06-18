Product: Integration tests for WSO2 ESB Wordpress connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

STEPS:

 1. Make sure the WSO2 ESB 4.9.0-ALPHA zip file available at "{ESB_Connector_Home}/repository/"

 2. This ESB should be configured as below;
	In Axis configurations (\repository\conf\axis2\axis2.xml).

   i) Enable following message formatters

                       <messageFormatter contentType="application/x-www-form-urlencoded"
                          class="org.apache.axis2.transport.http.XFormURLEncodedFormatter"/>

                       <messageFormatter contentType="multipart/form-data"
                          class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
   
   ii) Enable following message builders 

			<messageBuilder contentType="application/x-www-form-urlencoded"
                          class="org.apache.synapse.commons.builders.XFormURLEncodedBuilder"/>

                        <messageBuilder contentType="multipart/form-data"
                          class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 3. To obtain an access token go through the following steps,

	i) Go to "https://wordpress.com/wp-login.php?" and create your wordpress account.
       ii) Using "https://developer.wordpress.com/apps/new/" create a wordpress app, once configured you will receive your CLIENT ID and 	    CLIENT SECRET to identify your app.
      iii) Derive the access token by following the instructions at "https://developer.wordpress.com/2014/07/04/authentication-improvements-for-testing-your-apps/".

    NOTE - The method mentioned in step (iii) only available to the owner of the application, and not to any other user. This is meant for testing purposes only.To obtain access tokens for other users/blogs with a single app follow the instructions at "https://developer.wordpress.com/docs/oauth2/".

 4. Update the wordpress properties file located in "{ESB_Connector_Home}/wordpress/wordpress-connector/wordpress-connector-1.0.0/src/test/resources/artifacts/ESB/connector/config" with the site/blog domain and access token obtained from step 3.

 5.  Make sure that the wordpress connector is set as a module in esb-connectors parent pom.
        <module>wordpress/wordpress-connector/wordpress-connector-1.0.0</module>

 6. Navigate to "{PATH_TO_SOURCE_BUNDLE}/" and run the following command.
      $ mvn clean install

 NOTE : Following Wordpress account, can be used for run the integration tests.
    Username      : wso2esbconnector	
    password      : connector1234
    Client ID     : 35873
    Client Secret : MBRlOmkHETtJ6Y5nAjRfkAl0LDDVZhWEvYH3SowLJf2QJ5ndcoV0udK9qffkIkNA 
