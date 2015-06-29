Product: Integration tests for WSO2 ESB GoToWebinar connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 14.04, Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

Note:
	This test suite can be executed based on two scenarios.
		1. Use the given test account and parameters at the end of the file.
		2. Set up a new GoToWebinar account and follow all the instruction given below in step 5.

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

 2. Deploy relevant patches, if applicable.

 3. The ESB should be configured as below.
	i) Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

		Message Formatters :
		<messageFormatter contentType="application/vnd.citrix.g2wapi-v1.1+json"
                          class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
		<messageFormatter contentType="text/html"
                          class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		Message Builders :
		<messageBuilder contentType="application/vnd.citrix.g2wapi-v1.1+json"
                          class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
		<messageBuilder contentType="text/html"
						class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 4. Compress modified ESB as wso2esb- 4.9.0-ALPHA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".

 5. Create a Citrix account and obtain a client ID.
	i) 		Using the URL "https://developer.citrixonline.com/user/register" create a Citrix online account.
	ii)		Login to the Citrix account and go to 'My Apps' and add a new app.
	iii)	Select the above created app and obtain the Consumer Key.

 6. Create a GoToWebinar trial account and obtain the access token and the organizer_key.
	i)		Using the URL "https://secure.citrixonline.com/secure/gotowebinar/commerce/try/register" create a GoToWebinar free trial account using following details.
			a)Select number of organizer seats as 9.
			b)Enter the relevant information to create the account.
    ii)	    Follow the instructions in the 	"https://developer.citrixonline.com/page/direct-login" and make a GET request to the URL "https://api.citrixonline.com/oauth/access_token" passing the below parameters.
			a)grant_type -   Use "password".
			b)user_id    -   User email address that you used to create the GoToWebinar account.
			c)password 	 -   Password of your GoToWebinar account.
			d)client_id  -	 Use the Consumer Key you obtained under Step 4 iii).
	iii)	Get the ornanizer_key and the access token from the response.

 7. Pre-requisites for Integration tests

	i) 	Create and complete a webinar with more than one registrants (by clicking 'Registration URL') and sessions (Click on the scheduled webinar and add another session).
	ii) Create more than one upcoming webinars.

 8. Update the GoToWebinar properties file at location "{gotowebinar_connector_Home}/gotowebinar-connector/gotowebinar-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

	i)		apiUrl						-	Use the API URL as "https://api.citrixonline.com".
	ii)		accessToken					-	Place the access token from the generated in step 6 [iii].
	iii)	organizerKey				-	Place the ornanizer_key from the generated in in step 6 [iii].
	iv)		firstName					-	First name of the registrant.
	v)		lastName					-	Last name of the registrant.
	vi)		email						-	Email of the registrant(Should change before each run).
	vii)	firstNameOpt				-	First name of the registrant.
	viii)	lastNameOpt					-	Last name of the registrant.
	ix)		emailOpt					-	Email of the different registrant(Should change before each run).
	x)		organization				-	organization which registrant belongs.
	xi)		industry					-	Industry of the registrant.
	xii)	jobTitle					-	Job title of the registrant.
	xiii)   invalidUpcommingWebinarKey  -   Non existing webinar key.

 9. Make sure that the gotowebinar connector is set as a module in esb-connectors parent pom.
          <module>gotowebinar/gotowebinar-connector/gotowebinar-connector-1.0.0/org.wso2.carbon.connector</module>

 10. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
          $ mvn clean install

 NOTE : Following are the credentials for the GoToWebinar account used for integration tests.

	    email=sampathliynage@hotmail.com
	    password=1qaz2wsx@

