Product: Integration tests for WSO2 ESB yelp connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

1. Make sure the ESB 4.8.1 zip available at "{PATH_TO_SOURCE_BUNDLE}/yelp-connector/yelp-connector-1.0.0/repository/"

3. Make sure "integration-base" project is placed at "{PATH_TO_SOURCE_BUNDLE}/../"

3. Create a Yelp account and get the api access keys:
	i) 	Using the URL "http://www.yelp.com/SignUp" create a yelp account.
	ii) Get the api access keys from this url "http://www.yelp.com/developers/manage_api_keys".


4. Update the Yelp properties file at location "{PATH_TO_SOURCE_BUNDLE}/yelp-connector/yelp-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i)   consumerKey - Use the consumerKey you got from step 8.
        ii)  consumerKeySecret - Use the consumerKeySecret you got from step 8.
        iii) accessToken - Use the accessToken you got from step 8.
        iv)  accessTokenSecret - Use the accessTokenSecret you got from step 8.

		
5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/yelp-connector/yelp-connector-1.0.0/" and run the following command.
      $ mvn clean install


NOTE : Following Yelp account, can be used for run the integration tests.
    Username : yelpconnector2014@gmail.com
    Password : wso2@123
    consumerKey : cnLWLngTVVVq1IB_jY6ATg
    consumerKeySecret : EvogOq_n0wR3QD7P8XxLJbSyA60
    accessToken : 6ZejUmp_x306jkcvqujEDE4xGnBLl7Z9
    accessTokenSecret : SjebK4fuY6TAhF2Y1WngQnDcoEM

Yelp Connector Documentation link
    https://docs.google.com/document/d/15Se5Rr2E8f5AC_ko_Y5o4D0gYOBqu_dmUtZHpNBEwDU/edit