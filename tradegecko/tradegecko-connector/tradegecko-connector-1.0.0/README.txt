Product: Integration tests for WSO2 ESB Tradegecko connector

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
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new Tradegecko account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-Alpha from official website.
 
 2. Deploy relevant patches, if applicable.
 
 3. Follow the below mentioned steps for adding valid certificate to access Tradegecko API over https.

	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://api.tradegecko.com
	   
	ii)  Go to new ESB 4.9.0-Alpha folder and place the downloaded certificate in "<ESB_HOME>/repository/resources/security/"

	iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME" in command line to import Tradegecko certificate in to keystore. 
		 Give "wso2carbon" as password. Press "Y" to complete certificate import process.
		 
		 NOTE : CERT_FILE_NAME is the file name which was extracted from Tradegecko. (e.g. *.tradegecko.com)
			    CERT_NAME is arbitrary name for the certificate. (e.g. Tradegecko)

 4. Compress modified ESB as wso2esb-4.9.0-Alpha.zip and copy that zip file in to location "{Tradegecko_Connector_Home}/tradegecko-connector/tradegecko-connector-1.0.0/org.wso2.carbon.connector/repository/".

 5. Prerequisites for Tradegecko Connector Integration Testing

	a) Create a Tradegecko account using the URL "https://go.tradegecko.com/register".
		Note: This is a full featured 21-day free trial account.
	b) Create an application on Tradegecko using your account by navigating to https://go.tradegecko.com/oauth/applications. You will need to give an application name and callback URL for token requests. After successfully creating an application, obtain the Application ID and Application Secret.
	c) Navigate to https://api.tradegecko.com/oauth/authorize?response_type=code&client_id=<CLIENT_ID>&redirect_uri=<REDIRECT_URI> to obtain an authorization code. This flow will take you through screens to log in and allow the application to access data.
	d) Using the authorization code you received, send a POST request to https://api.tradegecko.com/oauth/token with the following JSON body: 

		{
		   "client_id":"YOUR_CLIENT_ID",
		   "client_secret":"YOUR_CLIENT_SECRET",
		   "redirect_uri":"YOUR_REDIRECT_URI",
		   "code":"AUTHORIZATION_CODE",
		   "grant_type":"authorization_code"
		}
	e) Obtain the access token that is returned for the above request. 

 6. Update the Tradegecko properties file at location "{Tradegecko_Connector_Home}/tradegecko-connector/tradegecko-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl					- Use the API URL as "https://api.tradegecko.com".
	ii)		accessToken				- Access Token obtained by following the steps in 5.
	iii) 	productId				- A product ID of a valid product.
	iv)		orderId					- ID of an order which is in 'finalized' state for mandatory test case.
	v)		orderLineItemId 		- ID of a line item of the order mapped to the order ID in iv).
	vi) 	orderIdOptional 		- ID of an order which is in 'finalized' state for optional test case.
	vii)	orderLineItemIdOptional - ID of a line item of the order mapped to the order ID in vi).
	viii)	billingAddressId		- Billing address ID of customer related to the order in vi).
	ix)		shippingAddressId		- Shipping address ID of customer related to the order in vi).
	x)		paymentTermId			- ID for a valid payment term from your account.
	xi)		stockVariantId			- Variant ID of a valid product for stock adjustment mandatory test case.
	xii)	optionalStockVariantId	- Variant ID of a valid product for stock adjustment optional test case.
	xiii)	stockLocationId			- A valid location ID from your account. 
	
 7.  7.Make sure that the Tradegecko connector is set as a module in esb-connectors parent pom.
              <module>tradegecko/tradegecko-connector/tradegecko-connector-1.0.0/org.wso2.carbon.connector</module>
      Navigate to "esb-connectors" and run the following command.
      $ mvn clean install

 Credentials of test account: 
 	username: wso2connector.abdera@gmail.com
 	password: 1qaz2wsx@