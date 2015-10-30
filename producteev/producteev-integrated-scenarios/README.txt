Product: Producteev Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-BETA-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
         -> producteev-connector-1.0.0
         -> pagerduty-connector-1.0.0
         -> bugherd-connector-1.0.0
         -> jotform-connector-1.0.0
         -> mandrill-connector-1.0.0
         -> zohocrm-connector-1.0.0

 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.

         Producteev  -  https://docs.wso2.com/display/CONNECTORS/Producteev+Connector
         Pagerduty   -  https://docs.wso2.com/display/CONNECTORS/PagerDuty+Connector
         Bugherd     -  https://docs.wso2.com/display/CONNECTORS/BugHerd+Connector
         Jotform     -  https://docs.wso2.com/display/CONNECTORS/JotForm+Connector
         Mandrill    -  https://docs.wso2.com/display/CONNECTORS/Mandrill+Connector
         ZohoCRM     -  https://docs.wso2.com/display/CONNECTORS/Zoho+CRM+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<PRODUCTEEV_CONNECTOR_HOME>/producteev-integrated-scenarios/src/common), to the ESB that are listed as below.
         - sequences -  faultHandlerSeq.xml
                        removeResponseHeaders.xml
  
         - templates -  responseHandlerTemplate.xml
                        retrieveTaskDetailsFromNotes.xml

 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.
 
 - Please note that the cases need to be executed in the same order as they appear in this document as they are inter-related and cannot be executed independently.

 - Common Assumptions :
               (a) All the won potentials exist in ZohoCrm, Producteev, Bugherd and Pagerduty accounts with the same name.
               (b) All the assignees (employees) are assumed to be existing in all the accounts (ZohoCRM,Producteev,Bugherd and PagerDuty).
               (c) Only the cases in status “New” will be considered for the scenario. Cases which are in status “Escalated”, “On hold” or “closed” will be skipped from ZohoCrm itself.
               (d) All the projects in the Producteev account belongs to one single Network.
               (e) All the incidents in the Pagerduty account belongs to one single service.
 
 - Common Prerequisites:
               (a) The following custom fields should be available in ZohoCRM,
                     For Cases: 
                        - Customized drop down list (Pick List) named as 'Task Label' having two values as 'Bug' and 'Production-Issue' to select the type of issue. 
                        - A text field named as 'Producteev Task ID'.
                     For Potentials:
                        - A text field named as 'Producteev Project ID'. Note: As described in Common Assumptions step (a) The won potential in ZohoCrm should hold the relevant Producteev project's ID in this custom field.
               (b) Two labels named as ‘Production-Issue' and ‘Bug’ needs to be created in the Producteev account.
               (c) Create a network in Producteev and note its network ID for further reference.
               (d) Create a service in Pagerduty and note its service key and service ID for further reference.

 01. Case Creation

    Case-001 -> 
      - Case Name:       Retrieve submissions and create cases.
      - Purpose:        (a) Retrieve form submission details from Jotform API and create new cases in ZohoCRM API.

      - Files:          (a) Proxy -    producteev_retrieveSubmisionsAndCreateCases.xml 
  
      - Prerequisites:  (a) Create a new form in JotForm API with the following fields.
                            (i)   'Your name'   - Text box to retrieve requester's name ( Make sure to enable "Required" to this form field).
                            (ii)  'Your email'  - Text box to retrieve requester's email address ( Make sure to enable "Required" to this form field and add 'Email' validation this field).
                            (iii) 'Type a brief subject about your query'  - Text box to retrieve the subject of the query ( Make sure to enable "Required" to this form field). 
                            (iv)  'Describe your query'  - Text area to  to retrieve the query details( Make sure to enable "Required" to this form field).
                            (v)   'Your project reference'  - Text box to retrieve the ZohoCRM potential ID. (Make sure to enable "Required" to this form field).
                        (b) Get at least one submission for the created form described under prerequisites (a).

      - Request Parameters: 
                        (a) jotform.formId  -  ID of the form created as prerequisites.
                         Note : All the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.

 02. Production Issue and Bug Tracking

    Case-001 -> 
      - Case Name:       Create tasks and add labels.
      - Purpose:        (a) Retrieve cases from the ZohoCRM API and create tasks in the Producteev API having them assigned to users and tag the created tasks under the relevant label.

      - Files:          (a) Proxy - producteev_createTasksAndAddLabels.xml

      - Prerequisites:  (a) There should be at least one user available in Producteev account other than the account owner, to be added as assignee.

      - Request Parameters: 
                        (a) producteev.assigneeIds - A comma separated list of IDs of the users(Other than the account owner) available in Producteev account.
                        (b) producteev.labelIds - An object containing IDs of the 'Bug' and 'Production-Issue' labels which are available in Producteev account as a prerequisite. Use the labels created under Common Prerequisites step (b).     
                         Note : All the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.
   
   03. Project Collaboration And Notification

    Case-001 -> 
      - Case Name:       Retrieve tasks and create issues.
      - Purpose:        (a) Retrieve active tasks from Producteev and create them either as bugs in Bugherd or incidents in Pagerduty.

      - Files:          (a) Proxy      - producteev_retrieveTasksAndCreateIssues.xml
                        (b) Sequences  - createBug.xml
                                         createIncident.xml
                                         retrieveTasksAndCreateIssues_verifyPrerequisites.xml

      - Request Parameters: 
                        (a) producteev.networkId - ID of the Producteev network. 
                        (b) pagerduty.serviceKey - API Key of the Pagerduty service.
                        (c) pagerduty.serviceId  - ID of the Pagerduty service.
                        (d) pagerduty.modifierId - ID of a team member who exists in Pagerduty account.
                         Note : All the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.

    Case-002 -> 
      - Case Name:       Update tasks and notify clients.
      - Purpose:        (a) Retrieve all the resolved bugs from Bugherd and resolved incidents from Pagerduty and update the relevant Producteev task with the resolution and mark them as 'resolved' in Producteev.
                        (b) Update the relevant cases in ZohoCrm with the solutions and mark the cases as ‘Closed’.
                        (c) Inform the requester of the case (case owner) about the closure of the case via Mandril API.
                        (d) Update the retrieved bug in Bugherd as 'Closed'.

      - Prerequisites:  (a) Assignee of the bug/incident should add a comment/note in Bugherd/Pagerduty respectively before executing this case in the format of 'Task Resolution:<The relevant resolution of the task>'. (e.g:- 'Task Resolution:Correct logo added with proper resolution.')
                        (b) Once the resolution is added, the bug's/incident's status needs to be changed as 'done' (in Bugherd) or 'resolved' (in Pagerduty).

      - Files:          (a) Proxy     - producteev_updateTasksAndNotifyClients.xml
                        (b) Sequences - processResolvedBugsAndNotifyRequester.xml
                                        updateTaskAndSendMessage.xml
                                       
      - Request Parameters: 
                        (a) pagerduty.serviceId  - ID of the Pagerduty service. Use the service's ID which is created under step (d) of 'Common Prerequisites'.
                        (b) mandrill.fromEmail   - Preferred email address of the sender in order to send emails in Mandrill.
                        (c) mandrill.fromName    - Preferred name of the sender in order to send emails in Mandrill.
                         Note : All the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.
      
    Case-003 -> 
      - Case Name:       Notify assignees about overdue tasks.
      - Purpose:        (a) Retrieve overdue tasks from Producteev API and send notification emails to task assignees with the task details.

      - Files:          (a) Proxy     - producteev_notifyAssigneesAboutOverdueTasks.xml
                        (b) template  - sendReminderEmail.xml
                                       
      - Request Parameters: 
                        (a) mandrill.fromEmail   - Notification email sender's email address.
                        (b) mandrill.fromName    - Notification email sender's name.
                         Note : All the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request. Refer to the individual connector methods for clarification.

   
