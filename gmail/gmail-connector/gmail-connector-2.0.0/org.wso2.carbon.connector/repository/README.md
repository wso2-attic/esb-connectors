ZOHO Invoice-Connector
================
Product: Integration tests for WSO2 ESB Nimble connector
Prerequisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform:

- Mac OS X V-10.9.5
- WSO2 ESB 4.8.9

STEPS:

1.Make sure the ESB 4.8.9 zip file with latest patches available at:
“Integration_Test\products\esb\4.8.9\modules\distribution\target\”.

2.Make sure the EsB 4.8.9 zip file with latest patches at:
“Integration_tets\Products\esb\4.8.9\modules\disribution”

3.Copy zoho connector zip file (zoho.zip) to the location:
"Integration_Test\products\esb\4.8.9\modules\integration\connectors\repository\"

4. Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file -
"Integration_Test\products\esb\4.8.9\modules\integration\connectors\src\test\resources\testng.xml"
 
<test name="zoho-Connector-Test" preserve-order="true" verbose="2>
<packages>
<package name="org.wso2.carbon.connector.integration.test.zoho"/>
</packages>
</test>
5. Copy proxy files to following location:
"Integration_Test\products\esb\4.8.9\modules\integration\connectors\src\test\resources\artifacts\ESB\config\proxies\zoho\"

6. Copy request files to following:
"Integration_Test\products\esb\4.8.9\modules\integration\connectors\src\test\resources\artifacts\ESB\config\restRequests\zoho\"

7. Edit the "zoho.properties" at:
Integration_Test\products\esb\4.8.9\modules\integration\connectors\src\test\resources\artifacts\ESB\connector\config using valid and relevant data. Parameters to be changed are mentioned below.

	- login https://www.zoho.com/login/ - you may use the dummy Account details below
	- request Auth token - To get the Auth token you have to follow the steps in this documentation manually https://accounts.zoho.com/apiauthtoken/create?SCOPE=ZohoInvoice/invoiceapi then use the value for Auth token.
	
8. Following data set can be used for the first testsuite run.

Proxy Directory Relative Path=/../src/test/resources/artifacts/ESB/config/proxies/zoho/
Request Directory Relative Path = /../src/test/resources/artifacts/ESB/config/restRequests/zoho/

proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/zoho/
requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/zoho/

Account Details:
username:watsan86@yahoo.com
password: 1234567

9. Navigate to "Integration_Test\products\esb\4.8.9\modules\integration\connectors\” and run the following command.
$ mvn clean install

