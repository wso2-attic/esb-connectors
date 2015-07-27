Product: Integration tests for WSO2 ESB MailChimp connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft Windows 7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Note:
	This Test Suite can be executed by setting up a new MailChimp trial account and following all the instruction given below.
	
Special Note:
	Please note that the following Mailchimp connector methods have not been added to the Mailchimp Integration Test Suite,
	Since they couldn't be orchestrated in the Test Suite due to them requiring manual involvement to run successful.
		-listCampaignClicks
		-listCampaignClickers
		-listCampaignOpeners

Steps to follow when executing the Test Suite:

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.

 3. ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
		
		<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
										  
		<messageBuilder contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

		<messageFormatter contentType="text/html"                             
							  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="text/html"                                
							  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".

5. Make sure that MailChimp is specified as a module in ESB Connector Parent pom.
        <module>mailchimp/mailchimp-connector/mailchimp-connector-1.0.0/org.wso2.carbon.connector</module>

6. Create a MailChimp account and obtain an API Key.
	i) 		Using the URL "https://login.mailchimp.com/signup?" create a MailChimp online account.
	ii)		Once an account is created a verification mail will be send to your mail account. Please click the Active Account URL link in the mail.
	iii) 	Once you have click the mail link it would take you to another page to finalize your MailChimp account creation.
	iv)	    Use the credentials given by you to login into MailChimp account and Click your profile name to expand the Account Panel, and choose Account.
	v)		Click the Extras drop-down menu and choose API keys.
	vi)		Copy an existing API key or click the Create A Key button.
    vii)	Name your key descriptively, so you know what application uses that key.
	viii) 	Create at least two Lists in the Mailchimp account you created before executing the Test Suite for the first time.
			(Once you login to your Mailchimp account, on your Welcome Screen, click on Lists on the left pane and then Click Create List to create lists)

			
 7. Update the MailChimp properties file at location "{MAILCHIMP_CONNECTOR_HOME}/mailchimp-connector/mailchimp-connector-3.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 									- 	Base endpoint URL of the Mailchimp API. Use the format "https://[dc].api.mailchimp.com" where <dc> is the the portion after the dash in your API Key. (e.g https://us2.api.mailchimp.com)
	ii)		apiKey									-	Use the api key obtained in Step 6(v).
	iii)	createTemplateMandatoryName				-	Name given for Template created in Mandatory Case.
	iv)		createTemplateOptionalName				-	Name given for Template created in Optional Case.
	v)		createTemplateNegativeName				-   Name given for Template created in Negative Case.
	vi)		createTemplateHTMLMandatory				-	HTML Content of the Template created in Mandatory Case.
	vii)	createTemplateHTMLOptional				-	HTML Content of the Template created in Optional Case.	
	viii)	createTemplateHTMLNegative				-	HTML Content of the Template created in Negative Case.
	ix)		folderName								-	Name of the folder to be created in Mailchimp during the test run.
	x)		campaignType							-	Type of campaign to be created in createDraftCampaign methods. Use "regular".
	xi)		createCampaignMandatoryText				-	Text to include in the Campaign created in Mandatory Case.
	xii)	createCampaignOptionalText				-	Text to include in the Campaign created in Optional Case.
	xiii)	createCampaignSubjectMandatory			-	Subject to include in the Campaign created in Mandatory Case.
	xiv)	createCampaignSubjectOptional			-	Subject to include in the Campaign created in Optional Case.
	xv)		createCampaignEmail						-	Email address to be used in createCampaign. (Need not be a real one - Stick to email format though)
	xvi)	createCampaignFromName					-	From name to be used while creating the campaign.
	xvii)	createCampaignToName					-	To name to be used while creating the campaign.
	xviii)	createCampaignTitleOptional				-	Optional Title to be used while creating campaign.
	xix)	email									-	Email address to be subscribed to the List. (Need not be a real one - Stick to email format though)
	xx)		emailOptional							-	Email address to be subscribed to the List. (Need not be a real one - Stick to email format though)
	xxi)	saveStatus								-	Status value assigned by the API for created campaigns which are not yet sent. Use "save"
	xxii)	sentStatus								-	Status value assigned by the API for sent campaigns. Use "sent"
	xxiii)	sleepTimeoutForSending					-	Time allowed for the campaign to be sent. Use "10000".
	xxiv)	updateEmailType							-	Type of the email preference (e.g. html)
	xxv)	mcLanguage								-	Language preferred by the member (e.g. en).
	xxvi)	updatedEmail							-	Email address to be updated in the memeber. (e.g. yas.mailchimp@gmail.com) 
	xxvii)	latitude								-	Latitude specified for the member's geographical location.
	xxviii)	longitude								-	Longitutde specified for the member's geographical location.
		
	* Properties iii), iv), v), ix), xiii), xiv), xix), xx), xxvi) needs to be changed for each execution of the Test Suite.
		Hint: Use a number along with some text and increment the number for each run to change property values.
	
 8. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install
	  
	  Note:- Account is in Trial mode (can only send campaigns to less than 100 emails).

		
