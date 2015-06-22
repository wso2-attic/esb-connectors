Product: Integration tests for WSO2 ESB Xero connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
            https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - Mac OSx 10.9
 - WSO2 ESB 4.8.1

Note:
	This test suite can be executed by setting up a new Xero account and following all the instruction given below in step 4.

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

 2. The ESB should be configured as below;
	i)  Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 	ii) Deploy relevant patches, if applicable.

 3. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".

 4. Create a Xero trial account and derive the Consumer Key and Consumer Secret.
	i) 		Using the URL "https://www.xero.com/signup/" create a Xero trial account setting the Business Location as 'United States'.
	ii)		Login to the created Xero account >> go to the Dashboard and add an Organization 
				a)Provide a name for the organization and select 'United States' under the caption 'Where does your organisation pay taxes?'.
				b)Click on 'Start Trial' and complete all the required fields in the Setup Guide Wizard and Finish setting up the organization.
	iii)	Provision the created Organization's Payroll account by Selecting the Organization >> Click 'Payroll' >> Click 'Get Started'.
	iv)		Complete all the tasks under 'Payroll' >> 'Overview'.
	v)		Create a payroll employee with the below details set.
				a) Set work location.
				b) Set social security number.
				c) Assign a pay schedule.
				d) Set salary details.
				e) Set the tax details.
	vi)		Go to the URL "https://api.xero.com/Application/" to add a public application.			
	vii)	Provide a valid Application name, URL of your company or product and a valid OAuth callback domain and Click the 'Save' button.
			OAuth callback domain can be added as explained in URL "http://developer.xero.com/documentation/advanced-docs/oauth-callback-domains-explained/".
	viii)	Fetch the 'Consumer Key' and the 'Consumer Secret' from the next page under 'OAuth Credentials'.

 5. Update the Xero properties file at location "{Xero_Connector_Home}/xero-connector/xero-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 					- 	The URL of the xero REST API (The default is "https://api.xero.com").
	ii) 	consumerKey				-   The consumer key obtained in Step 4 viii.
	iii)	consumerSecret			- 	The consumer secret obtained in Step 4 viii.
	iv)		accessToken				- 	The access token generated for the Xero payroll API using OAuth (v1.0a) protocol.
	v)		accessTokenSecret 		-	The access token secret generated with the above access token.
	vi)		firstName				-	Use a valid string value for the first name of the employee.
	vii)	lastName				-	Use a valid string value for last name of the employee.
	viii)	payScheduleId			-	Use a valid pay schedule ID.

	Note: Combination of first name and last name should be unique for each employee.
	
 6. Make sure that the xero connector is set as a module in esb-connectors parent pom.
          <module>xero/xero-connector/xero-connector-1.0.0/org.wso2.carbon.connector.xero</module>

 7. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
          $ mvn clean install

	  Note:- Xero trial account expires within 30 days.
