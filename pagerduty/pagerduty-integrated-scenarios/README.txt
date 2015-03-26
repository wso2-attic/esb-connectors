Product: Pagerduty Business Scenarios

Environment Setup:

 - Download and initialize ESB 4.8.1 .
 
 - Upload the following connectors to the ESB.
 
            -> PagerDuty-connector-1.0.0
            -> LiveChat-connector-1.0.0
            -> Nexmo-connector-1.0.0	
            -> Podio-connector-1.0.0
            -> SuveryMonkey-connector-2.0.0
            -> Gmail-connector-2.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            Pagerduty - https://docs.wso2.com/display/CONNECTORS/PagerDuty+Connector
            Livechat - https://docs.wso2.com/display/CONNECTORS/Livechat+Connector
            Nexmo - https://docs.wso2.com/display/CONNECTORS/Nexmo+Connector
            Podio - https://docs.wso2.com/display/CONNECTORS/Podio+Connector
            SuveryMonkey - https://docs.wso2.com/display/CONNECTORS/SurveyMonkey+Connector
            Gmail - https://docs.wso2.com/display/CONNECTORS/Gmail+Connector+Through+REST

 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<PAGERDUTY_CONNECTOR_HOME>/pagerduty-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consisted of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy and the template files which reside in the sub-folder, into the ESB.

Scenarios in Brief:   

  - IncidentCreation

    Case-001 -> 
            Scenario Name: Initiate Incident from Livechat.
            Description:   Retrieve tickets on a daily basis from LiveChat API and create an incident in PagerDuty API.
            Limitation:    Attachments in the Livechat API cannot be added to Pagerduty API since Pagerduty doesn't support attachments for incidents.
            Reference:     Scenario Guide Document -> Chapter 3.1

    Case-002 -> 
            Scenario Name: Initiate Incident from Gmail.
            Description:   Retrieve selected messages from Gmail API (where the client has reported the incidents) and create an incident in PagerDuty API.
            Pre requisite: Two labels should be created and named as 'Incidents' and 'Archived Incidents' in Gmail.
                           All the incident mails should be added under the Incidents label. Both label IDs should be passed as input parameters for the scenario.
                              gmailIncidentLabelId         - Incidents Label ID
                              gmailArchivedIncidentLabelId - Archived Incidents 
            Reference:     Scenario Guide Document -> Chapter 3.1
            Note:          Only text emails are processed. Messages should not contain new lines.
	
    Case-003 ->
            Scenario Name: Task Assignment.
            Description:   Reassign an incident in Pagerduty, send an SMS to the assignee using Nexmo and create a task in Podio for the incident.
            Pre requisite: The assignee in Pagerduty API must have configured with a contact method of type SMS.
            Reference:     Scenario Guide Document -> Chapter 3.1

  - IncidentTracking
  
    Case-001 ->
            Scenario Name: IncidentTracking.
            Description:   Complete the task in Podio, resolve the incident in PagerDuty and use the details to send a mail using Gmail to the client. 
                           Create a survey in SurveyMonkey and send the survey to the client.
            Pre requisite: A survey should be created in SurveyMonkey to be used as a template and that surveyID should be passed as the input parameter for surveyMonkeySurveyId in  the scenario. 
            Reference:     Scenario Guide Document -> Chapter 3.2
