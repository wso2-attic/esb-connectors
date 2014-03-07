Product: Integration tests for WSO2 ESB PayPal connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - WSO2 ESB 4.8.1
		  
STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test\products\esb\4.8.1\modules\distribution\target\".

2. This ESB should be configured as below;
	In Axis configurations (\repository\conf\axis2\axis2.xml).

   i) Enable message formatter for "text/html"
		<messageFormatters>
			<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>\
		</messageFormatters>

   ii) Enable message builder for "text/html"
		<messageBuilders>
			<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
		</messageBuilders>

2. Copy PayPal connector zip file (paypal.zip) to the location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\repository\"

3. Make sure the paypal test suite is enabled (as given below) and all other test suites are commented in the following file - "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\testng.xml"  
     <test name="PayPal-Connector-Test" preserve-order="true" verbose="2">
	<packages>

            <package name="org.wso2.carbon.connector.integration.test.paypal"/>

        </packages>
 
    </test>

4. Login to your developer account, then navigate to url: https://developer.paypal.com/webapps/developer/applications/accounts and create new sandbox account, by selecting "Account type" as personal and "Select payment card" as PayPal
   NOTE: This step is a pre-requisite of executing an approved payment.

5. Update the connector properties file "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\connector\config\paypal.properties" and modify clientId and clientSecret appropriately.

   i)  clientId and clientSecret - client id and client secret of registered application.

   ii) refreshToken - Execute the request described at https://developer.paypal.com/docs/api/#grant-token-from-authorization-code externally, and use the refresh token in the response. You may need to execute the web flow in order to get an authorization code which is required to this request.

   iii) paypalPaymentId_1 - Using the same access token obtained in iii), create a paypal payment using the PayPal REST API playground, and use the id in the response.

   vi) payerId_1 - In the above response, copy the URL with REDIRECT HTTP method, inside "links" array, and proceed with the web flow. After you are redirected to vendor website, use the PayerID in the URL.

   v) paypalPaymentId_2 and payerId_2 - Repeat the steps iv) and v) respectively.


6. Copy proxy files to location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\proxies\paypal\"

7. Copy request files to location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\restRequests\paypal\" 

8. Navigate to "Integration_Test\products\esb\4.8.1\modules\integration\connectors\" and run the following command.
     $ mvn clean install
