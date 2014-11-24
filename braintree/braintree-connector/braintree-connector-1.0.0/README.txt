Product: Integration tests for WSO2 ESB Braintree connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1
 
Steps to follow in setting integration test.
 1.  Download ESB 4.8.1 from official website.
 2.  Deploy relevant patches, if applicable.
 
	Special Note: Created transactions can be refunded only after they are settled. Settlement of transaction is a scheduled backend process which takes around a day to be processed.
	Due to this backend behavior, the transaction refunding functionality couldn't be incorporated successfully into the test suite. Therefore the methods,
	'testRefundTransactionWithMandatoryParameters' and 'testRefundTransactionWithOptionalParameters' are made to fail with a meaningful comment as follows:

	   'Braintree: Transaction needs to be settled before it can be refunded!
		Settlement of Transaction is a batch process which is executed by automated Scheduler.
		The following error is reported from the backend when trying to refund a transaction that has not yet been settled: ' followed by the SDK error message.
		
	The method(s) should be considered as passed if they fail with the above message. Any error messages apart from the above would indicate failure of the method(s).


STEPS: 

 1. Create an Account for Braintree Sandbox.
	i) Navigate to URL "https://sandbox.braintreegateway.com/login" 
	ii)Select Sign up and provide details for new user account in Sandbox environment and create a Braintree account. 
	   Eg:- Following account details were used to run integration tests.
			Username - virtusa.wso2.connector.dev@gmail.com
			Password - 1qaz2wsx@

 2. Obtain private key, public key and merchant Id from created Braintree account:	
	i)	 login to the account and navigate to Account => My user => click API keys (Autherization section).
	ii) Copy private key, public key and merchant Id as authentication details. 
		(See below details of virtusa.wso2.connector.dev@gmail.com account) 
	     Eg:- private key =  30b660a44ff94d99b31c9b80935169ea
		      public key = fnybvk2m9by8z6kd
			  merchant Id = x56bgbn75sq7qkfn  
	
 3. Create Custom fields for user account.
	i) Login to the created account in step(1) and navigate to Settings => Processing.
	ii) Create new Custom Field in Custom Fields section. 
	iii) Provide Api Name, Display name and select a given option. 
	iv) Copy the provided API names for step (6).
	
 4. Get master merchant account Id.
	i) Login to the created account in step(1) and navigate to Settings => Processing.
	ii) In the merchant account section copy the token related to default merchant account.
		(default merchant account is considered as "Master Merchant Account")
	
 5. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{BRAINTREE_CONNECTOR_HOME}/braintree-connector/braintree-connector-1.0.0/org.wso2.carbon.connector/repository/".

 6. Update the Braintree properties file at location "{BRAINTREE_CONNECTOR_HOME}/braintree-connector/braintree-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		privateKey - Use the private key you coppied from step (2) => (ii).
	ii) 	publicKey - Use the public key you coppied from step (2) => (ii).
	iii) 	merchantId - Use the merchant Id you coppied from step (2) => (ii).
	iv)     environment - Since you created account details for sandbox, keep the environment as SANDBOX. 
						  (Available environments are SANDBOX, DEVELOPMENT and PRODUCTION)
						  
	v) 		masterMerchantAccountId - Use the token you coppied in step(4) => (ii). 
	vi) 	timeDelay - Currently Braintree create transaction API expected rate is 30 seconds. Please make sure timeDelay >= 30000 (ms)
	vii)    customFields -  Created custom fields in step(3) => (iv) is used here. API name should be key and value can be any name. 
							Eg:- [{"apiName1":"value1"},{"apiName2":"value2"},{"apiName3":"value3"}]
	viii)	creditCardNumber - No. of a valid credit card. (Number should follow the right format eventhough it'll not be validated during creation.)
	ix)		expirationDate, expirationDateUpdate - Expiration date for the Credit card in the following format: MM/YYYY (future date)
	x)		cvv - Creditcard Verification Value of the Creditcard used.
	xi)		refundAmount - Amount to be refunded during Refund Transaction.
	xii)	expectedRefundExceptionMessage - Error message currently sent by the API for Refund Transaction failure.
	xiii)	planId - Id of a valid plan created through the Braintree Control Panel. Visit: https://sandbox.braintreegateway.com/merchants/<Your Merchant ID>/plans
	xiv)	opionalSubscriptionId, updateSubscriptionId - Subscription IDs used to create subscriptions (New values should be given each time the Test Suite is executed.)
	xv)		customerId - Valid customer registered to Braintree via Braintree Control Panel. Visit: https://sandbox.braintreegateway.com/merchants/<Your Merchant ID>/customers/new
	xvi)	resourceNotFoundException - Error message currently sent by the ESB for Not Found Exceptions thrown by Braintree SDK.
	xvii)	cardholderName - Any suitable string value.
			
 7. Navigate to "{BRAINTREE_CONNECTOR_HOME}/braintree-connector/braintree-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install 
