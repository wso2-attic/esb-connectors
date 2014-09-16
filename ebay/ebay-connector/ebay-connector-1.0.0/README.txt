Product: Integration tests for WSO2 ESB eBay connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

Note:
	This test suite can be executed based on two scenarios.
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new eBay account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.
 
 2. Deploy relevant patches, if applicable.
 
 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Ebay_Connector_Home}/ebay-connector/ebay-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Prerequisites for eBay Connector Integration Testing.

	i) 	 Create an eBay developer account using the URL "https://developer.ebay.com/join/".
	ii)	 Get the DevID, AppID and CertID from the eBay Developer Account Home using the URL "https://developer.ebay.com/DevZone/account"
	iii) Create an eBay sandbox account with eBay developer account[i] using the URL "https://developer.ebay.com/DevZone/sandboxuser/".
	iv)  Open an eBay sandbox store with eBay sandbox account[ii] using the URL "http://stores.sandbox.ebay.com/".
	v)   Create paypal sandbox test account using the URL "https://developer.paypal.com/webapps/developer/applications/accounts" and link it with the eBay developer sandbox account by giving ebay email address. 
    vi)  Get the Site Id and version from the eBay API Test Tool using the URL "https://developer.ebay.com/DevZone/build-test/test-tool/default.asp".
	
	Note:
	This test suite can be executed based on two scenarios.
	
		1. Use the given test account and parameters at the end of the file. - in this scenario must change the properties step : 5  viii, ix, x and xi  in the eBay properties file.
		
		2. Set up a new eBay developer account and follow all the instruction given below in step 5.
		
 
 5. Update the eBay properties file at location "{Ebay_Connector_Home}/ebay-connector/ebay-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		authToken 				- Generate an ebay auth token with developer DevID, AppID and CertID by using the URL "https://developer.ebay.com/DevZone/account/tokens/default.aspx".	
	ii) 	siteId					- Place the site Id that used to create a ebay store (step 4[vi]). 
	iii)	version					- Place the eBay API Compatibility level version (step 4[vi]). 
	iv)		appId					- Place the eBay developer account application Id (step 4[ii]).
	v)		tradingApiUrl			- Place the eBay trading api URL.
	vi)		shoppingApiUrl			- Place the eBay shopping api URL.
	vii)	routing					- Specify the routing as default.
	viii)	nameMandatory			- Name for the create store catagory with mandatory parameters.
	ix)		nameOptional			- Name for the create store catagory with optional parameters.
	x)		addItemTitle			- Title for the create item with mandatory parameters.
	xi)		addItemTitleOptional 	- Title for the create item with optional parameters.
	xii)	paypalEmailAddress		- eBay sandbox account linked paypal sandbox test account email address (step 4[v]). 
	xiii)	userId					- eBay sanbox account Id (e.g.: testuser_username).

 6. Navigate to "{Ebay_Connector_Home}/ebay-connector/ebay-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install


 NOTE : Following are the credentials for the eBay developer account used for integration tests.
		
		eBay developer account userId=samliyanage
		password=Zaq@4321
		
		eBay developer sandbox userId=TESTUSER_SAMLIYANAGE
		password=Zaq@4321
		