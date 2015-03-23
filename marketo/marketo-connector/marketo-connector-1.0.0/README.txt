Product: Integration tests for WSO2 ESB Marketo connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Mac OSx 10.9
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file available at "{MARKETO_CONNECTOR_HOME}/marketo-connector/marketo-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. This ESB should be configured as below;
	In Axis configurations (/repository/conf/axis2/axis2.xml).

    i) Enable message formatters for "application/json" and "text/html" in messageFormatters section
			
            <messageFormatter contentType="multipart/form-data” class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
			
   ii) Enable message builders for "application/json" and "text/html" in messageBuilders section
			
            <messageBuilder contentType="multipart/form-data" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 

 3. Create a marketo instance and get the clientId and clientSecret. See, "http://developers.marketo.com/blog/quick-start-guide-for-marketo-rest-api/"

	 
 4. Update the marketo properties file at location "{MARKETO_CONNECTOR_HOME}/marketo-connector/marketo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config/" as below.
 
      - marketoInstanceURL - Url of your marketo instance.

      - clientId - Use the clientId you got from step 3.
	
	  - clientSecret - Use the clientSecret you got from step 3.
	
      - accessToken - Use the accessToken you got from step 3.

      - Unique values for leadFirstName, leadLastName, leadEmail, leadFirstNameOptional1, leadLastNameOptional, leadEmailOptional1,
        leadFirstNameOptional2, leadLastNameOptional2, leadEmailOptional2, leadFirstNameOptional3,leadLastNameOptional3, leadEmailOptional3
   
      - programId - Create a new program in your marketo instance and get the ID.

      - listId - Create a new program in your marketo instance and get the ID. To get the ID of the list see, http://developers.marketo.com/documentation/rest/get-list-by-id/

      - listName - Name of the created list.

      - campaignId- Create a new smart campaign under the created program.

      - rCampaignId- Create a new smart campaign under the created program. The Smart Campaign must have a “Campaign is Requested” trigger with a Web Service API source.
    
 5. Navigate to "{MARKETO_CONNECTOR_HOME}/marketo-connector/marketo-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install


