Product: Freshdesk Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.8.1 .
 
 - Upload the following connectors to the ESB.
 
			-> freshdesk-connector-2.0.0
			-> disqus-connector-1.0.0
			-> nexmo-connector-1.0.0
			-> gmail-connector-1.0.0
			-> acivecollab-connector-1.0.0
			-> surveygizmo-connector-1.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            Freshdesk - https://docs.wso2.com/display/CONNECTORS/FreshDesk+Connector (v2 Not Available)
			Disqus - https://docs.wso2.com/display/CONNECTORS/Disqus+Connector
			Nexmo - Not Available
			Gmail - https://docs.wso2.com/display/CONNECTORS/Gmail+Connector
			ActiveCollab - https://docs.wso2.com/display/CONNECTORS/ActiveCollab+Connector
			SurveyGizmo - https://docs.wso2.com/display/CONNECTORS/SurveyGizmo+Connector
			
 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<Freshdesk_Connector_Home>/freshdesk-integrated-scenarios/src/common ), to the ESB that are listed as below.
			- sequences - 	faultHandlerSeq.xml
							removeResponseHeaders.xml	
			- templates - 	sendNotifications.xml
							responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			

 
 01. Ticket Tracking
	[i] Case-001
		- Purpose:	(a) Extract the unresolved or unclosed tickets of Freshdesk and notify the assignees through SMS via Nexmo API.
					(b) The notes of the retrieved tickets are created as tasks for the ticket in the Activecollab API in the given project.
					(c) An email will be sent to the assignee using the Gmail API containing the ticket details along with the task details.
		
		- Files:	(a) Proxy - <Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Ticket Tracking\Case-001\proxy\freshdesk_ticketAssignment.xml
		
		-Request Parameters:	(a) freshdeskTicketIds 		- One or more Freshdesk ticket Ids should be provided as an array.
								(b) resendSms 				- Mention whether or not it's required to send sms to the assignees to whom it is already been notified about the same ticket.
								(c) activeCollabProjectId 	- The project Id of ActiveCollab in which the Freshdesk notes should be added as tasks.						
		
		- Special Notes: (a) Initially few tickets should be created and assigned with assignees (agents) in Freshdesk as an offline process. Please note that assigning an agent for a ticket is MANDATORY for the scenario.
						 (b) Notes need to be added to tickets for those which are to be created as tasks in ActiveCollab.
						 (c) A project should be created in ActiveCollab offline.
						 (d) A custom field named 'SMSAlertSent' should be created for tickets in Freshdesk.
						 (e) A custom field named 'FreshdeskTicketId' should be created in ActiveCollab for tasks.
	[ii] Case-002
		- Purpose:	(a) Create a Survey in SurveyGizmo for tasks that need to be marked 'completed' in ActiveCollab.
					(b) Send the Survey to the requester via GMail.
					(c) Resolve the corresponding ticket in Freshdesk.
					(d) Complete the task in ActiveCollab.
		
		- Files:	(a) Proxy - <Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Ticket Tracking\Case-002\proxy\freshdesk_resolveTicketsAndSendSurvey.xml
					(b) Sequence - <Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Ticket Tracking\Case-002\sequence\resolveTicketsAndSendSurvey.xml
		
		-Request Parameters:	(a) activecollabProjectTasks 		- An array of objects in the following format.
											{
												"projectId":2, 				(ActiveCollab Project ID - Use only VALID project IDs)
												"completeAllTasks": true, 	(If 'true' is specified, all tasks belonging to the project will be considered for processing)
												"taskIds":[1,2] 			(Array of task IDs to process. This will be ignored if 'completeAllTasks' is set to 'true'. Use VALID values for taskIds)
											}
								(b) surveygizmoSurveyId - ID of the Survey created through the UI in SurveyGizmo. This survey will be used as a template to create surveys through the scenario.
		
		- Special Notes: (a) Initially few tickets should be created and assigned with assignees (agents) in Freshdesk as an offline process. Please note that assigning an agent for a ticket is MANDATORY for the scenario.
						 (b) There should be tasks created in ActiveCollab with the corresponding Freshdesk ticket ID given in the custom field 'FreshdeskTicketId'.
						 (c) A survey should be created in SurveyGizmo (through the UI).
						 (d) A custom field named 'SurveyGizmoID' should be created for tickets in Freshdesk.
						 (e) It is advisable to consider this case as a continuation of Case-001 of Ticket Tracking scenario and follow the special notes of Case 001 before executing Case-002.
						 (f) Please provide a VALID surveygizmoSurveyId.
						 
	[iii] Case-003
		- Purpose:	(a) Retrieve survey responses (from SurveyGizmo) for the tickets (in Freshdesk) and send it (via GMail) to the respective Agent.
					(b) Close the correspondng tickets in Freshdesk.
		
		- Files:	(a) Proxy - <Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Ticket Tracking\Case-003\proxy\freshdesk_sendSurveyResponseToAgent.xml
					(b) Sequence - <Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Ticket Tracking\Case-003\sequence\sendSurveyResponseToAgents.xml
		
		-Request Parameters:	(a) freshdeskTickets 				- An array of Freshdesk Ticket IDs which needs to be closed. 
								(b) freshdeskResolvedTicketsViewId 	- A view can be created for 'Resolved' tickets in freshdesk and provide the ID of the view for the parameter. (Ignored if 'freshdeskTickets' is provided at least one ID)
		
		- Special Notes: (a) There should be tickets in Freshdesk with state 'Resolved'. Those tickets should contain the ID of the Survey created for them (in SurveyGizmo) in the custom field 'SurveyGizmoID'.
						 (b) The Survey should've been taken (answered) for the response to be available.
						 (c) It is advisable to consider this case as a continuation of Case-002 of Ticket Tracking scenario and follow the special notes of Case-002 before executing Case-003.
						 (d) Please provide a VALID view ID for freshdeskResolvedTicketsViewId.
 02. Ticket Reminders
 
	[i] Case-001
		- Purpose:	(a) Retrieve tickets which are 'overdue' and/or priority 'urgent' from Freshdesk and notify the assignees (agents) through email and/or SMS.
					(b) If (a) is successful for 'overdue' tickets -> Send an email to the assignees (agents) about the overdue tickets.
					(c) If (a) is successful for 'urgent' tickets -> Send sms to assignees (agents) about the 'urgent' tickets.
		
		- Files:	(a) Proxy - 	<Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Ticket Reminders\Case-001\proxy\freshdesk_sendNotificationsToAgents.xml

		
		-Request Parameters:	(a) Freshdesk - Tickets details on 'overdue' and/or priority 'urgent' from Freshdesk. 
													- freshdeskOverdueTicketsViewId - Overdue tickets view Id.
													- freshdeskUrgentTicketsViewId - Urgent tickets view Id.
								(b) Gmail - Send emails to assignees (agents) on overdue tickets.
													- gmailUsername - Gmail username for authentication.
													- gmailOauthAccessToken - Gmail authtoken for authentication.
		
		- Special Notes: View Id's can be created on Freshdesk to query 'overdue' and 'urgent' tickets.
							The API behavior when providing INVALID view Ids is not predictable. Therefore please make sure only valid view Ids are used while executing the scenarios.
	
 03. Topic Collaboration
	[i] Case-001
		- Purpose:	(a) Create a Topic in Freshdesk API (Title and Message provided along with the request).
					(b) If (a) is successful -> Create a Thread in Disqus with the same Title and Message.
		
		- Files:	(a) Proxy - 	<Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Topic Collaboration\Case-001\proxy\freshdesk_createTopicAndThread.xml
		
		-Request Parameters:	(a) freshdeskForumId - Unique identifier of the forum in Freshdesk API where the Topic should be created.
								(b) freshdeskTitle - Title of the Topic to be created. (The same will be used as the title of the thread in Disqus).
								(c) freshdeskBodyHtml - Message of the Topic to be created. (The same will be used as Thread message in Disqus).
								(d) freshdeskCategoryId - ID of the category to which the forum in freshdesk belongs.
								(e) disqusForumId - Unique identifier of the Disqus forum in which the Thread will be created.
		
		- Special Notes: (a) URL for the newly created Disqus Thread will be returned as part of the response, which can be directly used to access the Thread.
		
	[ii] Case-002
		- Purpose:	(a) Update the entries of a particular Topic in Freshdesk, with the Top posts (filtered by No. of votes) from the corresponding Thread created in Disqus.
		
		- Files:	(a) Proxy - <Freshdesk_Connector_Home>\freshdesk-integrated-scenarios\src\scenarios\Topic Collaboration\Case-001\proxy\freshdesk_updateTopicWithTopPosts.xml
		
		-Request Parameters:	(a) freshdeskForumId - Unique identifier of the forum in Freshdesk API where the Topic was created.
								(b) freshdeskDisqusIdMap - An array of JSON objects, each specifying the mapping between Freshdesk Topic ID and the corresponding Disqus Thread ID in the following format.
									{ "freshdesk_topicId": "157", "disqus_threadId": "3278671494" }. Multiple such mapping objects can be passed onto the Array in one execution of the scenario.
								(c) freshdeskCategoryId - ID of the category to which the forum in freshdesk belongs.
								(d) disqusTopPosts - Specify how many top posts should be chosen from Disqus Thread (in the descending order of No. of votes).
								    E.g. If disqusTopPosts = 3, then the Topic in Freshdesk will be updated with best 3 posts from the corresponding Thread in Disqus.
								(e) freshdeskDisqusIdMap - Unique identifier of the Disqus forum in which the Thread was created.
		
		- Special Notes:	(a) For the scenario to produce any results, Threads in Disqus should have voted Posts (at least 1).
							(b) Please note that Disqus API takes certain amount of time to update the vote count for a post, from the time a post was voted (between 3 to 5 minutes).
							(c) The id object returned as part of the response of Topic Collaboration - Case 01 can be directly passed on to the freshdeskDisqusIdMap array.		
							(d) The order of entries under the Topic in Freshdesk after the scenario has been executed is unpredictable (Will not be ordered by No. of points each entry has).