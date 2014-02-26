Product: Integration tests for WSO2 ESB PayPal connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - WSO2 ESB 4.8.1
		  
STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test\products\esb\4.8.1\modules\distribution\target\".

2. This ESB should configured below
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

4. Get the access token by the following URL : https://devtools-paypal.com/hateoas/index.html and update "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\connector\config\paypal.properties" and modify clientId and clientSecret appropriately.
NOTE: this access is subject to expire and at the event of expiration the user need to replace the access token following the above steps.

5. Copy proxy files to location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\proxies\paypal\"

6. Copy request files to location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\restRequests\paypal\" 

7. Update request files with relevant data if necessary.

8. Navigate to "Integration_Test\products\esb\4.8.1\modules\integration\connectors\" and run the following command.
     $ mvn clean install
