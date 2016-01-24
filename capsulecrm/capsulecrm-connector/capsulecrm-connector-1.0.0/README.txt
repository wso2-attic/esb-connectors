Product: Integration tests for WSO2 ESB CapsuleCRM connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 5. Make sure that capsulecrm is specified as a module in ESB_Connector_Parent pom.
 	<module>capsulecrm/capsulecrm-connector/capsulecrm-connector-1.0.0/org.wso2.carbon.connector</module>

 6. Create a CapsuleCRM trial account and derive the API Token.
	i) 	Using the URL "https://app.capsulecrm.com/signup/free" create a CapsuleCRM free trial account.
	ii)	Login to CapsuleCRM account dashboard using the URL "https://[subdomain].capsulecrm.com/login" and navigate to "My Preferences" > "API Authentication Token"  under the logged user to obtain the API authentication token.
	
 7. Prerequisites for CapsuleCRM Connector Integration Testing

	i) 	Navigate to the URL "https://[subdomain].capsulecrm.com/parties", add one or more person and add one or more organisation to the account.
	ii) Go to "Sales Pipeline" page using the URL "https://[subdomain].capsulecrm.com/pipeline", click "Select this template" under the "Simple" template to set milestone values as "New", "Bid", "Won", and "Lost". 
 
 8. Update the CapsuleCRM properties file at location "<CAPSULECRM_CONNECTOR_HOME>/capsulecrm-connector/capsulecrm-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created CapsuleCRM account (e.g. https://[subdomain].capsulecrm.com).
	ii) 	apiToken						-   Use the API authentication token obtained under step 6 ii).
	iii)	owner							-   Place an authenticated user to create opportunities and cases (Use the URL "http://developer.capsulecrm.com/v1/resources/users/" for the instructions, to get the list of authenticated users).
	iv)		opportunityNameMand				-	A String value for the opportunity name to create an opportunity with mandatory parameters. 
	v)		opportunityNameOpt				-	A String value for the opportunity name to create an opportunity with optional parameters. 
	vi)		currency						-	3 character ISO currency code (e.g. USD).
	vii)	OpportunityDescription			-	A String value for the opportunity description to create an opportunity with optional parameters.
	viii)	caseNameMand					-	A String value for the case name to create a case with mandatory parameters.
	ix)		caseNameOpt						-	A String value for the case name to create a case with optional parameters.
	x)		caseDescription					-	A String value for the case description to create a case with optional parameters.

  9. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install

		