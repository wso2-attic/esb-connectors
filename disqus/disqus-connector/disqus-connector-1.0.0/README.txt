Product: Integration tests for WSO2 ESB Disqus connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-Alpha

Note:
	This test suite can be executed based on two scenarios.
	
	1. Set up a new Disqus account and follow all the instruction given below in step 4.
	2. Use the given test account and parameters at the end of the file.
		
Special Note : 	
	Please note that the following Disqus connector method have not been added to the Disqus Integration Test Suite,
    since it couldn't be automated due to it requiring manual involvement to run successful.
	- listForumUsers

 
Steps to follow in setting integration test.

 1. Download ESB 4.9.0-Alpha from official website.
 
 2. Deploy relevant patches, if applicable.

 3. Extract the certificate from browser by navigating to "https://www.disqus.com/" and place the certificate file in following locations.

 	i)  "<DISQUS_CONNECTOR_HOME>/disqus-connector/disqus-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

 		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "disqus"' in command line to import disqus  certificate in to keystore. Give "wso2carbon" as password.
 		NOTE : CERT_FILE_NAME is the file name which was extracted from disqus with  the extension, change it accordingly. Remove the copied certificate.

 	ii) "wso2esb-4.9.0-Alpha/repository/resources/security"

 		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "disqus"' in command line to import disqus certificate in to keystore. Give "wso2carbon" as password.
 		NOTE : CERT_FILE_NAME is the file name which was extracted from disqus with  the extension, change it accordingly. Remove the copied certificate.
 
 4. Compress modified ESB as wso2esb-4.9.0-Alpha.zip and copy that zip file in to location "{Disqus_Connector_Home}/disqus-connector/disqus-connector-1.0.0/org.wso2.carbon.connector/repository/".

 5. Prerequisites for Disqus Connector Integration Testing.

	i) 	 Create an Disqus account using the URL "https://disqus.com/profile/signup/".
	ii)	 After creating and verifying account navigate to URL "https://disqus.com/api/applications/" to register application.
	iii) Register an application by filling required fields.
	iv)  Once it completed go to "https://disqus.com/api/applications/" and click on newly created Applications.
	v)   In the details tab you will be able to find required OAuth Settings of you application.(Make sure that your application has both Read and Write access, if not update you application with both access permissions).
    vi)	 Go to Admin page using URL "http://disqus.com/admin/"
	vii) Click on the menu dropdown on the left hand corner and click on "Add new site". 
	viii)Enter a Site name.
	ix)  Enter an unique Disqus URL (e.g. testforum), select an appropriate category and click on the "Finish Registration" button.
	x)   Then the page will be redirect to your new unique URL like "https://testforum.disqus.com/admin/settings/install/". Keep sub-domain name to use as a forumId (e.g. testforum). 
 
 6. Update the Disqus properties file at location "{Disqus_Connector_Home}/disqus-connector/disqus-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl				- API Url of Disqus
	ii) 	apiKey				- Place API Key which you can find under step 4 [v]. 
	iii)	accessToken			- Place Access Token which you can find under step 4 [v]. 
	iv)		forumId				- Place then forumId, which retrieved in step 4 [x].
	v)		title				- Any title for new Thread.
	vi)		message				- Message for new Thread.
	vii)	esbSubscribeEmail	- Subscriber Email.
	viii)	apiSubscribeEmail	- Subscriber Email address, should be different address from vii).
	ix)		sinceDate			- Any past date in following format (yyyy-MM-dd) to retrieve forum categories.
	x)		order				- Should be 'asc' or 'desc' to list forum categories according to given order.
	

 7. Make sure that the disqus connector is set as a module in esb-connectors parent pom.
              <module>disqus/disqus-connector/disqus-connector-1.0.0/org.wso2.carbon.connector</module>

      Navigate to "{ESB_Connector_Home}/" and run the following command.
            $ mvn clean install


 NOTE : Following are the credentials for the Disqus used for integration tests.
		
		Disqus account user name=janakar
		password=janakar
		
		