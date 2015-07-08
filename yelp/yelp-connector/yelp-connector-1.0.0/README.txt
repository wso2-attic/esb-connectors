Product: Integration tests for WSO2 ESB yelp connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
               https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

1. Make sure the ESB 4.9.0-ALPHA zip available at "{<ESB_CONNECTORS_HOME>/repository/"

2. Create a Yelp account and get the api access keys:
	i) 	Using the URL "http://www.yelp.com/SignUp" create a yelp account.
	ii) Get the api access keys from this url "http://www.yelp.com/developers/manage_api_keys".


3. Update the Yelp properties file at location "{ESB_Connector_Home}/yelp-connector/yelp-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i)   consumerKey - Use the consumerKey you got from step 8.
        ii)  consumerKeySecret - Use the consumerKeySecret you got from step 8.
        iii) accessToken - Use the accessToken you got from step 8.
        iv)  accessTokenSecret - Use the accessTokenSecret you got from step 8.

		
4. Make sure that the yelp connector is set as a module in esb-connectors parent pom.
        <module>yelp/yelp-connector/yelp-connector-1.0.0</module>

5. Navigate to "<ESB_Connector_Home>" and run the following command.
         $ mvn clean install


NOTE : Following Yelp account, can be used for run the integration tests.
    Username : yelpconnector2014@gmail.com
    Password : wso2@123
    consumerKey : cnLWLngTVVVq1IB_jY6ATg
    consumerKeySecret : EvogOq_n0wR3QD7P8XxLJbSyA60
    accessToken : 6ZejUmp_x306jkcvqujEDE4xGnBLl7Z9
    accessTokenSecret : SjebK4fuY6TAhF2Y1WngQnDcoEM
