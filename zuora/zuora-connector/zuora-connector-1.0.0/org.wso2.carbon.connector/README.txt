Product: Integration tests for WSO2 ESB Zuora connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform:

 - MAC OS
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0
 - Java 1.7

Steps to follow in setting integration test.

 1. Create a Zuora account and get user credentials according to below steps:

	i)  create a account in Zuora and login.
		(Note the APIURl for further use. "https://www.zuora.com/" )
	ii) create a customer account
	iii)create a product catalog and get productRatePlanChargeId, productRatePlanId
	iv) create a subscription

 2. Download ESB WSO2 ESB 4.9.0

 3.	Navigate to {ESB_HOME}/repository/conf/axis2.xml, add the following message Builder
	<messageBuilder contentType="text/html"
		class="org.wso2.carbon.relay.BinaryRelayBuilder"/> and
	add the following Message Formatter
	<messageFormatter contentType="text/html"
		class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

 4. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 5. Make sure that Zuora is specified as a module in ESB_Connector_Parent pom.
	<module>Zuora\zuora-connector\zuora-connector-1.0.0\org.wso2.carbon.connector</module>

 6. Update the 'zuora.properties' file at the location "{ZUORA_CONNECTOR_HOME}/zuora-connector/zuora-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
	 i)   apiUrl                  -  Api url of Zuora
	 ii)  apiVersion              -  Version of the Zuora API
	 iii) apiAccessKeyId          -  The account username of Zuora
	 iv)  apiSecretAccessKey      -  The account password of the Zuora
	 v)   accountKey              -  Account key created in (ii)
	 vi)  subscriptionKey         -  SubscriptionKey created in (iii)
	vii)  productRatePlanChargeId -  ProductRatePlanChargeId created in (ii)
	viii) productRatePlanId       -  productRatePlanId created in (ii)
	ix)   contractEffectiveDate   -  Effective contract date for your subscription

	*	subscriptionKey value should be changed in each run.

 7. Navigate to "{ESB_Connector_Home}/" and run the following command.
	  $ mvn clean install