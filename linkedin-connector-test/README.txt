Product: Integration tests for WSO2 ESB LinkedIn connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- Microsoft WINDOWS V-7
- WSO2 ESB 4.8.1
		  
STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test\products\esb\4.8.1\modules\distribution\target\".

2. Copy Linkedin connector zip file (linkedin.zip) to the location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\repository\"

3. Uncomment LinkedIn test suite and comment the other test suites of the following file - "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\testng.xml"

4. Copy proxy files to location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\proxies\linkedin\"

5. Copy request files to location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\restRequests\linkedin\" 

6. Edit the "linkedin.properties" at "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\connector\config\" using valid and relevant data. Parameters to be changed are mentioned below.
	
	- accessToken: Get the access token by the following URL :http://developer.linkedin.com/documents/authentication.
	- myPublicUrl: public URL which belongs to the access token of the test account. 
	- memberId: A valid id of a connection.
	- publicProfileUrl: public profile URL of a connection (for other accounts).
	- companyId: A valid id of a company.
	- jobId: A valid job id.
	- followCompanyId: A valid id of a company, which a user needs to follow. This parameter is applicable only for the "followCompanyPage". Same id cannot be used twice.  

7. Navigate to "Integration_Test\products\esb\4.8.1\modules\integration\connectors\" and run the following command.
     $ mvn clean install
