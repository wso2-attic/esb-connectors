Product: Integration tests for WSO2 ESB Sendloop connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7
 
STEPS:

 1.   Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.   Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
      If required add the X.509 certificate from https://<domain-ID>.sendloop.com (follow step 4 to obtain the domain ID) to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
      and wso2carbon.jks located in <SENDLOOP_CONNECTOR_HOME>/sendloop-connector/sendloop-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.

 3.   Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml".
   
      <messageFormatter contentType="application/x-javascript"
                          class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
      <messageFormatter contentType="text/html"
                        class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
   
      <messageBuilder contentType="application/x-javascript"
                        class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
      <messageBuilder contentType="text/html"
                        class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
                        
      Compress the modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "<ESB_CONNECTOR_HOME>/repository/"
 
 4.   Follow the below steps to create a Sendloop account and derive the domain ID and API key.
   i)    Navigate to "https://sendloop.com/signup", enter the required information and click on "Create Your Account" button.
   ii)   Once in your account dashboard, obtain and retain your domain ID from the URL for later use.
         E.g. If dashboard URL is http://c3c1c1b-5ecabf.sendloop.com, then the domain ID is 'c3c1c1b-5ecabf'.
   iii)  In your account dashboard, click on 'Settings' -> 'API Settings' -> Copy and retain your API Key for later use.
   iv)   In your account, create a subscriber list with any name and add at least two subscribers with valid email addresses to it.
   v)    In your account, click on Emails -> Send Email -> Code your own email -> Launch HTML Editor -> Follow the below steps.
            a) Clear the editor and paste the following HTML code snippet into it.
            '<p>Get the puppies now itself... Limited time offer!</p><p>To order a puppy now <a href='http://www.awesomequotes4u.com/'>Click Here</a></p><p><strong>Unsubscription information:</strong><br>This email has been sent to %Subscriber:EmailAddress%<br><a href='%Link:Unsubscribe%'>Click here to unsubscribe now</a></p>'
            b) Type in a valid subject line.
            c) Choose the subscriber list created in 4. iv) for recipients.
            d) Give a from name and email that is allowed by the API.
            e) Click 'Send Now' send the email campaign.
            f) Go to the dashboard (homepage), click on Emails -> Click on the email campaign you just created -> Copy and retain its ID from the URL.
            g) Check for emails in the subscriber email accounts and once received, click on the embedded links (Click from at least two emails is required).
 
 5.   Update the Sendloop properties file at location "<SENDLOOP_CONNECTOR_HOME>/sendloop-connector/sendloop-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   i)       apiUrl                              - Base endpoint URL of the API. Use https://<domain-ID>.sendloop.com. For domain ID use what was obtained in 4. ii)
   ii)      apiKey                              - Authentication Key to access the API. Use what was obtained in 4. iii)
   iii)     format                              - Format of the APi response. Use 'json'.
   iv)      mandatoryEmailCampaignName          - Campaign name used for createEmailCampaign mandatory case.
   v)       optionalEmailCampaignName           - Campaign name used for createEmailCampaign optional case.
   vi)      mandatoryEmailCampaignFromName      - Campaign from name used for createEmailCampaign mandatory case.
   vii)     mandatoryEmailCampaignFromEmail     - Campaign from email address used for createEmailCampaign mandatory case. Use a properly formatted email address. E.g. besafe.abdera@gmail.com
   viii)    mandatoryEmailCampaignReplyToName   - Campaign reply to name used for createEmailCampaign mandatory case.
   ix)      mandatoryEmailCampaignReplyToEmail  - Campaign reply to email address used for createEmailCampaign mandatory case. Use a properly formatted email address. E.g. besafe.abdera@gmail.com
   x)       mandatoryEmailCampaignSubject       - Campaign subject used for createEmailCampaign mandatory case.
   xi)      optionalEmailCampaignSubject        - Campaign subject used for createEmailCampaign optional case.
   xii)     optionalEmailCampaignPlainContent   - Campaign content used for createEmailCampaign optional case. Use 'Text Campaign Body %Link:Unsubscribe%'
   xiii)    mandatoryEmailCampaignHtmlContent   - Campaign content used for createEmailCampaign mandatory case. Use '<p>Get the puppies now itself... Limited time offer!</p><p>To order a puppy now <a href=''http://www.awesomequotes4u.com'>Click Here</a></p><p><strong>Unsubscription information:</strong><br>This email has been sent to %Subscriber:EmailAddress%<br><a href='%Link:Unsubscribe%'>Click here to unsubscribe now</a></p>'
   xiv)     campaignCampaignNameUpdated         - Updated campaign name used for updateEmailCampaign method.
   xv)      campaignFromNameUpdated             - Updated campaign from name used for updateEmailCampaign method.
   xvi)     campaignFromEmailUpdated            - Updated campaign from email address used for updateEmailCampaign method.
   xvii)    campaignReplyToNameUpdated          - Updated campaign reply to name used for updateEmailCampaign method.
   xviii)   campaignReplyToEmailUpdated         - Updated campaign reply to email address used for updateEmailCampaign method.
   xix)     campaignSubjectUpdated              - Updated Campaign Subject used for updateEmailCampaign method.
   xx)      subscriberListName                  - Name of the subscriber list.
   xxi)     subscriberListOptInMode             - Mode of the subscriber list. Use 'Double'
   xxii)    subscriberListNameUpdated           - Updated name of the subscriber list.
   xxiii)   subscriberListOptInModeUpdated      - Mode of the subscriber list. Use 'Single'
   xxiv)    mandatorySubscriberEmailAddress     - Email address of the subscriber. Use a properly formatted email address. E.g. subscriber.email.mandatory001@gmail.com
   xxv)     optionalSubscriberEmailAddress      - Email address of the subscriber. Use a properly formatted email address. E.g. subscriber.email.optional001@gmail.com
   xxvi)    optionalSubscriptionIp              - IP address of the subscriber. Use a properly formatted IP address. E.g. 192.36.32.69
   xxvii)   subcriberEmailAddressUpdated        - Updated email address of the subscriber. Use a properly formatted email address. E.g. updated.email.mandatory001@gmail.com
   xxviii)  clickedCampaignId                   - ID of the campaign obtained in 4. v) f).
   
   Property values need not be changed between subsequent executions of the test suite.
   
 6.   Make sure that Sendloop is specified as a module in ESB Connector Parent pom.
         <module>sendloop\sendloop-connector\sendloop-connector-1.0.0\org.wso2.carbon.connector</module>

 7.   Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install