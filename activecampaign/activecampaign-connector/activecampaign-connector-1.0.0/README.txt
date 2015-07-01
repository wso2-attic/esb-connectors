Product: Integration tests for WSO2 ESB ActiveCampaign connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

   Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04, Mac OSx 10.9
    - WSO2 ESB 4.9.0-ALPHA
    - Java 1.7

STEPS:

1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/.

2. Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
    <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

3. Follow the below mentioned steps to create a new ActiveCampaign account:

   i)   Navigate to the following URL and create an account in ActiveCampaign: https://www.activecampaign.com/signup
   ii)  Log In to the account, navigate to My Settings > API page and obtain the API URL and the API Key for further use.
   iii) Create a campaign which contains a URL to be clicked by the receivers and send it to a contact list which contain at least two contacts. Retrieve the ID of the campaign and the ID of the message related to the campaign for further use.
           Tip to find the message Id: Click the specific campaign --> Click Message from the menus in left --> You can find the message Id from the un-subscribe link.
   iv)  Make sure the the URL of the campaign has been clicked by at least one of the receivers.

4. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

5. Make sure that activecampaign is specified as a module in ESB_Connector_Parent pom.
    <module>activecampaign/activecampaign-connector/activecampaign-connector-1.0.0/org.wso2.carbon.connector</module>

6. Update the property file activeCampaign.properties found in {ActiveCampaign_Connector_Home}/activecampaign-connector/activecampaign-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:

   i)   apiUrl         -  API URL to which the service calls are made obtained in Step 3 - ii. e.g. https://anusoft.api-us1.com.
   ii)  apiKey         -  Use the API key obtained in Step 3 - ii.
   iii) subject        -  Use a valid string value for the subject.
   iv)  email          -  Use a valid email address.
   v)   campaignId1    -  The unique Id for the campaign obtained from Step 3 - iii.
   vi)  messageId1     -  The unique Id for the message created for the campaign obtained from Step 3 - iii.
   vii) emailMandatory -  Use a valid and a unique email address.
   viii)emailOptional  -  Use a valid and a unique email address.
   ix)  firstName      -  Use a valid string value for the first name of a contact.
   x)   orgName        -  Use a valid string value for the organization name.
   xi)  name           -  Use a valid string value for the campaign name.
   xii) type           -  Type of the campaign. default value to be used is 'single'.

   Note: The property values of subject, emailMandatory, emailOptional should be changed to unique different values for each integration execution.

7. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install