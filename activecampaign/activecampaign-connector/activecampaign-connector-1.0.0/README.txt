Product: Integration tests for WSO2 ESB ActiveCampaign connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

   Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
	- WSO2 ESB 4.9.0-BETA-SNAPSHOT
	- Java 1.7
	
Steps to follow in setting integration test.

 1. Download ESB WSO2 ESB 4.9.0-BETA-SNAPSHOT by navigating the following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/.
 
 2.	Deploy relevant patches, if applicable and the ESB should be configured as below.
	Please make sure that the below mentioned Axis configurations are enabled(<ESB_HOME>/repository/conf/axis2/axis2.xml).

    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
    <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
	
3. Follow the below mentioned steps to create a new ActiveCampaign account:

   i)	Navigate to URL "http://www.activecampaign.com/free/" and create a trial account. Once you registered it automatically navigates to "https://{company}xxx.activehosted.com/overview/".
   ii)	Navigate to My Settings > API page and obtain the API URL and the API Key for further use.		
   iii) Create a campaign which contains a URL to be clicked by the receivers and send it to a contact list which contain at least two contacts. Retrieve the ID of the campaign and the ID of the message related to the campaign for further use.
   iv)  Make sure the the URL of the campaign has been clicked by at least one of the receivers.
   v) 	Create at least one pipeline.
   
4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

6. Make sure that ActiveCampaign is specified as a module in ESB_Connector_Parent pom.
 	<module>activeCampaign\activecampaign-connector\activecampaign-connector-1.0.0\org.wso2.carbon.connector</module>

7. Update the property file activeCampaign.properties at the location "{ACTIVECAMPAIGN_CONNECTOR_HOME}/activecampaign-connector/activecampaign-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below:
   i)    apiUrl         			-  Use the api url, step 3->(ii)
   ii)   apiKey         			-  Use the api url, step 3->(ii)
   iii)  subject        			-  A String value without spaces for subject.
   iv)   email          			-  A valid email address.
   v)    campaignId1    			-  The unique Id for the campaign obtained from Step 3 - (iii).
   vi)   messageId1     			-  The unique Id for the message created for the campaign obtained from Step 3 - (iii).
   vii)  emailMandatory 			-  A valid and a unique email address.
   viii) emailOptional  			-  A valid and a unique email address.
   ix)   firstName      			-  A String value without spaces for the first name of a contact.
   x)    orgName        			-  A String value without spaces for the organization name.
   xi)   name           			-  A String value without spaces for the campaign name.
   xii)  type           			-  Type of the campaign, default value to be used as 'single'. Valid values: 'single', 'recurring', 'split', 'responder', 'reminder', 'special', 'activerss', 'text'.
   xiii) dealTitleMandatory			-  A String value without spaces for the deal title.
   xiv)  dealTitleOptional			-  A String value without spaces for the deal title.
   xv)   orgName					-  A String value without spaces for the organization name.
   xvi)  dealStageTitleMandatory	-  A String value without spaces for the deal stage title.
   xvii) dealStageTitleOptional		-  A String value without spaces for the deal stage title.
   xviii)color						-  HEX value of header color of the new deal stage. Example: 'FF0000'
   ix)   dealValue					-  The value of the new deal in dollars
   x)    dealCurrency				-  Currency of the new deal. Example: 'usd'

   Note: The property values of subject, emailMandatory, emailOptional should be changed in each run.
   
8. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install