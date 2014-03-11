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

3. Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file - "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\testng.xml"

	<test name="LinkedIn-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.linkedin"/>
        </packages>
    </test> 

4. Copy proxy files to following location "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\proxies\linkedin\"

5. Copy request files to following "Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\config\restRequests\linkedin\" 

6. Edit the "linkedin.properties" at Integration_Test\products\esb\4.8.1\modules\integration\connectors\src\test\resources\artifacts\ESB\connector\config using valid and relevant data. Parameters to be changed are mentioned below.
	
	- accessToken: Use the sandbox account created below no-7. Get the access token by the following URL :http://developer.linkedin.com/documents/authentication. 
	- Alternatively you can also follow the below mentioned steps to get the access token through apigee console.
		- Navigate to https://apigee.com/console/linkedin
		- Under “Authentication” select Oauth2
		- Select sign in with linkedIn
		- Provide ur username and password on re-directed page and click allow access
		- It will come back to apigee page and click send (request URL should https://api.linkedin.com/v1/people/~)
		- You will have access token in respose		
	- myPublicUrl: public URL which is belongs to the test account. (Use the redirected URL here. Eg: http://www.linkedin.com/pub/darshana-silva/91/b8b/3a1)
	- memberId: A valid memner id. [this member must be a connection of the test account].
	- publicProfileUrl: public profile URL of a connection (for other accounts). (Use the redirected URL here. Eg: http://www.linkedin.com/in/janakaranathunga)
	- companyId: A valid id of a company.
	- jobId: A valid job id.
	- followCompanyId: A valid id of a company, which a user needs to follow. This parameter is applicable only for the "followCompanyPage". Please give a new company id for followCompanyId parameter each time you run the integration tests.  

		Special Notes:
		
		- Following requirements should be fulfilled, in order for "getStatus" test cases to be successful.
			- Test account should update the "status" before proceed with the test
			- Connection account referred by "memberId" in the properties file should have a valid status update.
		
		- When a particular method is executed several times, throttle limit which is defined by the API will be exceeded and the method will be blocked.
			This can be a reason for test cases to fail.
		
7. Following data set can be used for the first testsuite run.

		proxyDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/proxies/linkedin/
		requestDirectoryRelativePath=/../src/test/resources/artifacts/ESB/config/restRequests/linkedin/
		accessToken=<Generate the access token>
		myPublicUrl=http://www.linkedin.com/pub/darshana-silva/91/b8b/3a1
		memberId=G5B_qSKwNw
		publicProfileUrl=http://www.linkedin.com/in/janakaranathunga
		companyId=66028
		jobId=12642318
		followCompanyId=567779
			(followCompanyId: A valid id of a company, which a user needs to follow. This parameter is applicable only for the "followCompanyPage". Please give a new company id for followCompanyId parameter each time you run the integration tests.)
		Account Details: 
		username: dsilvawso2@gmail.com
		password: Pathfinder92@feb
8. Navigate to "Integration_Test\products\esb\4.8.1\modules\integration\connectors\” and run the following command.
     $ mvn clean install
