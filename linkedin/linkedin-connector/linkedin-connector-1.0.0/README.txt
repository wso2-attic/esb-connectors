Product: Integration tests for WSO2 ESB LinkedIn connector
Pre-requisites:

- Maven 3.x
- Java 1.6 or above

Tested Platform: 

- Microsoft WINDOWS V-7
- UBUNTU 14.04
- WSO2 ESB 4.9.0

STEPS:

1. Extract the certificate from browser by navigating to https://www.linkedin.com/ and place the certificate file in following locations.

	i)  "<ESB_Connector_Home>/linkedin/linkedin-connector/linkedin-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "LinkedIn"' in command line to import linkedin certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from linkedin with  the extension, change it accordingly. Remove the copied certificate.

	ii) "WSO2-ESB-4.9.0/repository/resources/security"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "LinkedIn"' in command line to import linkedin certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from linkedin with  the extension, change it accordingly. Remove the copied certificate.

2). Edit the "linkedin.properties" at Integration_Test\products\esb\4.9.0\modules\integration\connectors\src\test\resources\artifacts\ESB\connector\config using valid and relevant data. Parameters to be changed are mentioned below.
	
	- accessToken: Use the sandbox account created below no-3. Get the access token by the following URL :http://developer.linkedin.com/documents/authentication.
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
		
3). Following data set can be used for the first testsuite run.

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

4) Make sure that the LinkedIn connector is set as a module in esb-connectors parent pom.
      <module>linkedin/linkedin-connector/linkedin-connector-1.0.0/org.wso2.carbon.connector</module>

5) Navigate to "{ESB_Connector_Home}/" and run the following command.
       $ mvn clean install

