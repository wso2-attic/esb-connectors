Product: SupportBee Integrated Scenarios

Environment Set-up:

 - Download and initialize WSO2 ESB 4.9.0 - SNAPSHOT .
 
 - Upload the following connectors to the ESB.
 
			-> formstack-connector-1.0.0
			-> agilezen-connector-1.0.0
			-> supportbee-connector-1.0.0
			-> mandrill-connector-1.0.0
			-> zohocrm-connector-2.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            Formstack 	-	https://docs.wso2.com/display/CONNECTORS/Formstack+Connector
			AgileZen	-	https://docs.wso2.com/display/CONNECTORS/AgileZen+Connector
			SupportBee	-	https://docs.wso2.com/display/CONNECTORS/SupportBee+Connector
			Mandrill  	-	https://docs.wso2.com/display/CONNECTORS/Mandrill+Connector
			ZohoCRM 	- 	https://docs.wso2.com/display/CONNECTORS/Zoho+CRM+Connector
			
 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory(inside <SUPPORTBEE_CONNECTOR_HOME>/supportbee-integrated-scenarios/src/common), to the ESB that are listed as below.
			- sequences - 	faultHandlerSeq.xml
							removeResponseHeaders.xml
			- templates - 	responseHandlerTemplate.xml	
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			

 - Please note that the cases need to be executed in the same order as they appear in this document as they are inter-related.
 
 01. Ticket Initiation
	[i] Case-001
		- Purpose:	(a) Retrieve cases based on the case type ('Problem' and 'Feature Request') from the Zoho CRM API and create tickets in the SupportBee.
					(b) Retrieve unread bug report submissions from the FormStack API and create tickets in the SupportBee API and mark the submission as read in the FormStack API.
					(c) After creating the tickets in the SupportBee API, assign it to a user. 
					
		- Files:	(a) Proxy - supportbee_InitiateTickets.xml
					(b)	Sequence - assignUsersToTickets.xml
								 - createTicketsFromSubmissions.xml		
		- Request Parameters:   
					(a) agentIds - A string of VALID agentIds separated by commas.	
					(b) bugReportFormId - ID of the form that is created in Formstack (mentioned in step (c) in prerequisites).
								
					* All other parameters are specific to respective connectors and are self-explanatory. Refer to the individual connector methods for clarification.
					
					Note that all the rest of the parameters are apiUrls and authentication credentials to access each API specified in the request.
	    - Prerequisites - (a)At least one Case is required in ZohoCrm account having set the following required fields. 
								- Subject
								- Reported By
								- Email	
								- Description (Do not include any line breaks in the description)
								- Type		  
						   (b)Have at least one agent in the supportBee account having set a valid email address.
						   (c)Create a form in FormStack with the following fields
								- Name (Name field type)
								- Email (Email Address field type)
								- Subject (Short Answer field type)
								- Content (Long Answer field type)
								- CC-Email (Email Address field type)
								- Notify User (Check box field type with one option)
						   (d)There should be a custom field added to Cases in ZohoCrm named as 'SupportBee Ticket ID'.
	 Note:
		- 'Cases' cannot be handled in a 'Standard ZohoCrm Plan' thus a trial account of 'Professional ZohoCrm Plan' or 'Enterprise ZohoCrm Plan' will be required.
		- When creating tickets in SupportBee by retrieving cases of ZohoCrm, only the 'Feature Request' and 'Problem' type cases will be considered and all the rest of the case types (e.g:- Question) will be ignored.
		- When providing a set of supportBee 'agentIds' in the request please make sure that you provide valid agentIds from the supportBee account. If not it's not guaranteed that all the tickets will be assigned with assignees since some of them will be skipped.
		- When creating the bug report form in FormStack as an offline process, please make sure that the below fields are created in the correct order for the case to work accurately
				(1)Name 
				(2)Email
				(3)Subject
				(4)Content
				(5)CC-Email
				(6)Notify User
				
 02. Process Tickets
 
	[i] Case-001
		- Purpose:	(a) Label a tickets as 'enhancement'. If the ticket has already been added the 'enhancement' label, all the actions listed here are skipped.
					(b) Create a story for each ticket (which were labelled as 'enhancement') in AgileZen, under the given project.
					(c) Send an acknowledgement email to the requester.					
		
		- Files:	(a) Proxy - <SUPPORTBEE_CONNECTOR_HOME>/supportbee-integrated-scenarios/src/scenarios/Process Tickets/Case 001/proxy/supportbee_createStoryAndSendNotification.xml
		
		- Request Parameters:   
					(a) agilezen.size - Size of the story (Optional - Applicable to all the stories which will be created in the same execution).
					(b) agilezen.priority - Priority of the story (Optional - Applicable to all the stories which will be created in the same execution).
					(c) agilezen.color - Color of the story (Optional - Applicable to all the stories which will be created in the same execution).
					(d) agilezen.projectId - VALID ID of the project under which the stories will be created - Mandatory.
					(e) mandrill.fromEmail - Sender email address for the acknowledgement email - Mandatory.
					(f) mandrill.fromName - Sender name address for the acknowledgement email - Mandatory.
					(g) ticketIds - List of the VALID SupportBee ticket IDs which are to be created as stories - Mandatory.
								
					* All other parameters are specific to respective connectors and are self-explanatory. Refer to the individual connector methods for clarification.
		
		- Special Notes: 	
					(a) Prior to the execution of the case, a label called 'enhancement' should be created in SupportBee account. 

		
	[ii] Case-002
		- Purpose:	(a) Retrieve 'Completed' stories from AgileZen and do the following for each story,
						(i)   Close (reply to) the associated SupportBee ticket with the resolution given as a comment for the story.
						(ii)  If the ticket is associated with a ZohoCRM case, close the case with resolution.
						(iii) Create a survey (clone from the provided template survey) and send it to the requester.
						(iv)  Archive the associated SupportBee ticket.
						(v)   Archive the story in AgileZen.
							
		- Files:	(a) Proxy - <SUPPORTBEE_CONNECTOR_HOME>/supportbee-integrated-scenarios/src/scenarios/Process Tickets/Case 002/proxy/supportbee_replyTicketAndSendSurvey.xml
					(b) Template - <SUPPORTBEE_CONNECTOR_HOME>/supportbee-integrated-scenarios/src/scenarios/Process Tickets/Case 002/templates/agilezen_getPhaseIdFromName.xml
								 - <SUPPORTBEE_CONNECTOR_HOME>/supportbee-integrated-scenarios/src/scenarios/Process Tickets/Case 002/templates/formstack_updateFieldContent.xml
								 
		- Request Parameters:	(a) formstack.templateFormId - Template form ID which will be used as base to create survey forms to send to the user.
								(b) formstack.subjectFieldName - Label of the ticket subject field in the template form (as given in Special Notes (b) (i)).
								(c) formstack.detailsFieldName - Label of the ticket details field in the template form (as given in Special Notes (b) (ii)).
								(d) formstack.resolutionFieldName - Label of the ticket resolution field in the template form (as given in Special Notes (b) (iii)).
								(e) agilezen.projectId - VALID ID of the project whose stories need to be processed - Mandatory.
								(f) mandrill.fromEmail - Sender email address for the acknowledgement email - Mandatory.
								(g) mandrill.fromName - Sender name address for the acknowledgement email - Mandatory.
								
								* All other parameters are specific to respective connectors and are self-explanatory. Refer to the individual connector methods for clarification.
		
		- Special Notes: (a) Completed stories in AgileZen MUST have a single-line comment which starts with '[Resolution]' and follows with the resolution.
							 (All comments added to stories in AgileZen should be single-line comments.)
						 (b) Create a form in FormStack with the following fields
								(i)   Ticket Subject (Short answer field type - Read only)
								(ii)  Ticket Details (Long answer field type - Read only)
								(iii) Subject (Long Answer field type - Read only)
								(iv)  Any number of multiple choice questions (Radio button type)
		
	[iii] Case-003
		- Purpose:	(a) Retrieve 'Archived' tickets from SupportBee and do the following for each ticket,
						(i)   Retrieve the survey associated with the ticket and verify whether the requester has taken the survey.
						(ii)  If the survey has been taken at least once, retrieve the most recent submission and send the feedback to the agent (in SupportBee).
						(iii) Trash the ticket.
						(iv)  Delete the survey in Formstack.
							
		- Files:	(a) Proxy - <SUPPORTBEE_CONNECTOR_HOME>/supportbee-integrated-scenarios/src/scenarios/Process Tickets/Case 003/proxy/supportbee_sendFeedbackToAgents.xml
		
		- Request Parameters:	(a) formstack.questions - Array of question in the survey form in the following structure: {"index":"", "question":""}
										index - Position of the question in the Survey (1-based index)
										question - Question string.
									Please note that only the questions specified here will be included in the feedback email sent to the agent.
								(b) mandrill.fromEmail - Sender email address for the acknowledgement email - Mandatory.
								(c) mandrill.fromName - Sender name address for the acknowledgement email - Mandatory.
								
								* All other parameters are specific to respective connectors and are self-explanatory. Refer to the individual connector methods for clarification.
		
		- Special Notes: (a)  Surveys need to be taken for the scenario to be successfully executed.
		
Note:- Do not use line breaks while entering values for ZohoCrm fields which can possibly have multiple lines. e.g:-  When creating a new case, do not use line breaks in 'Description' filed.		