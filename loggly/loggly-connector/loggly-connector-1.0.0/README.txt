Product: Integration tests for WSO2 ESB Loggly connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches are available at "{LOGGLY_CONNECTOR_HOME}/loggly-connector/loggly-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. The ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

    <messageFormatter contentType="multipart/form-data"
                            class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

    <messageBuilder contentType="text/html"	class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

    <messageBuilder contentType="multipart/form-data"
                            class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

	Note: Add the aforementioned message formatter and the message builder to the axis file, if they are not available by default.

 3. Follow the below steps to create a Loggly account.

	i) Navigate to "https://www.loggly.com/" and click "Try Loggly for FREE" link.
   ii) Enter the required details and complete the account creation.

 4. Required properties for Loggly Connector Integration Testing

	i) logglyAccountApiUrl 		- The URL of the account subdomain. This parameter takes the format of "http://<account>.loggly.com". Substitute your account name for "<account>".
	ii) logglyApiUrl            - The API URL. Current API URL is "http://logs-01.loggly.com".
	iii) token 				    - The customer token. Follow the steps in 5) to derive the customer token.
	iv) txtFileName 		    - Name of the file which is used in the uploadLogFile method. File is located at "{LOGGLY_CONNECTOR_HOME}/loggly-connector/loggly-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/config/resources/loggly/".
	v) username 			    - Username of the loggly account. This will be used to generate the Basic HTTP Access Authentication Token mentioned in Step 5).
	vi) password            	- Password of the loggly account. This will be used to generate the Basic HTTP Access Authentication Token mentioned in Step 5).
	vii) query 			        - A random string which will be used to query the logs. Eg: "abc".
	viii) firstLine 	        - A random string value which will be logged Eg: "aaa".
	ix) secondLine 				- A random string value which will be logged Eg: "bbb".

 5. This API uses two types of authentication schemes. Those two types are authentication via Customer Token and Basic HTTP Access Authentication.

	i) Deriving the customer token:

		- Navigate to loggly account home page.
		- Go to "Source Setup" > "Customer Tokens" and get the customer token which is required to execute the uploadLogFile and sendBulkLogs methods.

   ii) Basic HTTP Access Authentication:

		- The below steps will be done through the code. It is sufficient to provide the username and the password in the properties file.
		- Concatenate the username and password according to the below format.
			username:password
		- Encode (Base64) the entire string and get the token.

 6.Make sure that the shopify connector is set as a module in esb-connectors parent pom.
               <module>loggly/loggly-connector/loggly-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Navigate to "{LOGGLY_CONNECTOR_HOME}/loggly-connector/loggly-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install