Product: Integration tests for WSO2 ESB PagerDuty connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

   Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1

STEPS:

1. Download ESB 4.8.1 from official website.

2. Deploy relevant patches, if applicable and the ESB should be configured as below.
   Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
    <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

3. Follow the below mentioned steps to create a new PagerDuty account:

   i)   Navigate to the following URL and create an account in PagerDuty: https://signup.pagerduty.com/accounts/new
   ii)  Note down the Site Address provided which will used as the API URL.
   iii) Log In to the account, navigate to API Access page, create new API Key and obtain the API Key and save it for further use.
   iv)  Navigate to My Profile and retrieve the user ID from the address bar of the browser for further use.
   v)   Navigate to Services > Add New Service, add a service for 'Use our API directly' integration type and retrieve the Service API Key from the 'Integration Settings' category.
   vi)  Navigate to Escalation Policies > New Escalation Policy, create a new escalation policy and retrieve the policy ID from the address bar of the browser for further use.

4. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{PagerDuty_Connector_Home}/pagerduty-connector/pagerduty-connector-1.0.0/org.wso2.carbon.connector/repository/".

5. Update the property file pagerduty.properties found in {PagerDuty_Connector_Home}/pagerduty-connector/pagerduty-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:

   i)   apiUrl                -  API URL to which the service calls are made. e.g. https://virasoft.pagerduty.com.
   ii)  apiToken              -  API Key which is used as the security token obtained in Step 3 - iii.
   iii) requesterId           -  Use the user ID retrieved in Step 3 - iv.
   iv)  escalationPolicyId    -  The escalation policy ID created in Step 3 - vi.
   v)   userEmail             -  A unique email address for a user.
   vi)  userName              -  A unique name for a user.
   vii) userEmailOpt          -  A unique email address for a user.
   viii)userNameOpt           -  A unique name for a user.
   ix)  userRole              -  A valid role of a user. (Use limited_user).
   x)   serviceKey            -  Use the service API Key obtained in Step 3 - v.
   xi)  description           -  Use a valid string value for the description of the incident.
   xii) incidentKey           -  Use a valid unique string for the key of the incident.
   xiii)incidentKeyOpt        -  Use a valid unique string for the key of the incident.
   xiv) contactMethodLabel    -  Use a valid string value for the label of the contact method.
   xv)  contactPhoneNumber    -  Use a valid phone number.
   xvi) incidentNote          -  Use a valid string value for the note to be added to an incident.
   xvii)timeOut               -  Use a valid number of milliseconds to wait till the request has effected. (Recommended value is 5000)

   Note: The property values of userEmail, userName, userEmailOpt, userNameOpt, incidentKey and incidentKeyOpt should be changed to unique different values for each integration execution.

6. Navigate to "{PagerDuty_Connector_Home}/pagerduty-connector/pagerduty-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install