Product: Integration tests for WSO2 ESB PayPal connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - Ubuntu 12.04, Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7
		  
STEPS:

1. Download ESB 4.9.0-ALPHA and copy the wso2esb-4.9.0-ALPHA.zip in to location "{ESB_Connector_Home}/repository/".

2. This ESB should be configured as below;
	In Axis configurations (\repository\conf\axis2\axis2.xml).

   i) Enable message formatter for "text/html"
		<messageFormatters>
			<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		</messageFormatters>

   ii) Enable message builder for "text/html"
		<messageBuilders>
			<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
		</messageBuilders>

3. Login to your developer account, then navigate to url: https://developer.paypal.com/webapps/developer/applications/accounts and create new sandbox account, by selecting "Account type" as personal and "Select payment card" as PayPal
   NOTE: This step is a pre-requisite of executing an approved payment.

4. Update the connector properties file "{Paypal_Connector_Home}\src\test\resources\artifacts\ESB\connector\config\paypal.properties" and modify clientId and clientSecret appropriately.

   i)  clientId and clientSecret - client id and client secret of registered application.

   ii) refreshToken - Execute the request described at https://developer.paypal.com/docs/api/#grant-token-from-authorization-code externally, and use the refresh token in the response. You may need to execute the web flow in order to get an authorization code which is required to this request.

   iii) paypalPaymentId_1 - Using the same access token obtained in iii), create a paypal payment using the PayPal REST API playground, and use the id in the response.

   iv) payerId_1 - In the above response, copy the URL with REDIRECT HTTP method, inside "links" array, and proceed with the web flow. After you are redirected to vendor website, use the PayerID in the URL.

   v) paypalPaymentId_2 and payerId_2 - Repeat the steps iv) and v) respectively.

   NOTE: Please follow steps iii), iv) and v) for subsequent test runs, after initial test run.

5. Make sure that paypal is specified as a module in ESB_Connector_Parent pom.
      <module>paypal/paypal-connector/paypal-connector-1.0.0/org.wso2.carbon.connector</module>

9. Navigate to "{ESB_Connector_Home}/" and run the following command.
     $ mvn clean install

NOTE : Following PayPal sand box account can be used for run the integration tests.
Username : ppalconnector@gmail.com
Password : paypalCon123

