Product: ActiveCollab Business Scenarios

Environment Setup:

 - Download and initialize ESB 4.8.1 .
 
 - Upload the following connectors to the ESB.
 
			-> activecollab-connector-1.0.0
			-> freshdesk-connector-1.0.0
			-> bugherd-connector-1.0.0	
			-> zohocrm-connector-2.0.0
			-> googletasks-connector-1.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            ActiveCollab - https://docs.wso2.com/display/CONNECTORS/ActiveCollab+Connector
			FreshDesk - Not Available
			Bugherd - https://docs.wso2.com/display/CONNECTORS/BugHerd+Connector
			ZohoCRM - https://docs.wso2.com/display/CONNECTORS/ZohoCRM+Connector
			Google Tasks - https://docs.wso2.com/display/CONNECTORS/Google+Tasks+Connector
			
 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<ActiveCollab_Connector_Home>/activecollab-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consists of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy, sequence and the template files which reside in the sub-folder, into the ESB.
		  
 - Special Note: If ActiveCollab API returns any unexpected error messages as part of the scenario, append ':443' to the end of activecollabApiUrl when executing the Scenarios.
 
Scenarios in Brief:   

  - Project Initiation

	Case-001 -> 
			Scenario Name: Create Activecollab clients.
			Description: Creating Activecollab client accounts by retrieving relevant contact from the ZohoCRM.
			Pre requisite: ZohoCRM contact's Account name should be match with existing Activecollab company name. If not client creation operation will be skipped for particular contact.
			Special Note: Request parameter description is as follows.
				- zohoApiUrl - API URL for ZohoCRM.
				- zohoAccessToken - Access token for ZohoCRM. 
				- zohoScope - ZohoCRM scope should be "crmapi".
				- zohoNewFormat - The newFormat, an integer determine weather null values should be excluded(1) or included(2).
				- zohoVersion - The API version Ex. 1 .
				- activecollabApiUrl - API URL for ActiveCollab account.
				- activecollabApiToken - API Access token for ActiveCollab account user. User access level should be 'Read and Write' and not 'Read only'. 
				- activecollabPassword - Password client Account.
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 1 
			
	Case-002 -> 
			Scenario Name: Create Activecollab projects.
			Description: Create Projects in ActiveCollab by retrieving potentials that were won in Zoho CRM. 	
			Pre requisite: ZohoCRM contact's Account name should be match with existing Activecollab company name. If not client creation operation will be skipped for particular contact.
			Special Note: Request parameter description is as follows.
				- zohoApiUrl - API URL for ZohoCRM.
				- zohoAccessToken - Access token for ZohoCRM. 
				- zohoScope - ZohoCRM scope should be "crmapi".
				- zohoNewFormat - The newFormat, an integer determine weather null values should be excluded(1) or included(2).
				- zohoVersion - The API version Ex. 1 .
				- activecollabApiUrl - API URL for ActiveCollab account.
				- activecollabApiToken - API Access token for ActiveCollab account user. User access level should be 'Read and Write' and not 'Read only'. 
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 1  

  - Project Tracking			
  
	Case-001 ->
			Scenario Name: Creating a task in GoogleTasks to a task created in Activecollab.
			Description: Retrieves the task details from Activecollab.
						 Creates a task in GoogleTasks using the details, derived by Activecollab.
			Pre requisite: Should create a custom field in the task to track google task id. This name can be use as the value of "activecollabTaskDuplicateTrackerFieldName" in the request.
			Special Note: Request parameter description is as follows.
				- activecollabApiUrl - API URL for ActiveCollab account.
				- activecollabApiToken - API Access token for ActiveCollab account user. User access level should be 'Read and Write' and not 'Read only'. 
				- activecollabProjectId - Activecollab project id used to get tasks.
				- activecollabTaskDuplicateTrackerFieldName - Name of the custom field which will be updated with google task id after creating the task.
				- googleTasksAccessToken - API Access token for GoogleTasks account user, With selecting 'manage your tasks URL and view your tasks URL'.
				- googleTasksTasklistId - The task list id used to create tasks. 
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 2
	
	Case-002 ->
			Scenario Name: Updating Activecollab task according to task updates in GoogleTasks.
			Description: Retrieves the task details from GoogleTasks.
						 Updates the task in Activecollab using the details, derived by GoogleTasks.
			Pre requisite: There has to be a task in GoogleTasks.
			Special Note: Request parameter description is as follows.
				- activecollabApiUrl - API URL for ActiveCollab account.
				- activecollabApiToken - API Access token for ActiveCollab account user. User access level should be 'Read and Write' and not 'Read only'. 
				- activecollabFormat - Use json as the value. 
				- activecollabProjectId - Id of the Activecollab project to which the tasks in the tasksIdMap belong.
				- tasksIdMap - Give the tasks id map as follows;
						{"GoogleTasks Task ID":"Activecollab Task ID"}
				- googleTasksAccessToken - API Access token for GoogleTasks account user, With selecting 'manage yor tasks URL and view your tasks URL'.
				- googleTasksTasklistId - The task list id used to get tasks. 	
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 2
			
  - Project Collaboration			
  
	Case-001 ->
			Scenario Name: Retrieve Tasks from ActiveCollab and create a corresponding task in FreshDesk/Bugherd.
			Description: Retrieve Task from ActiveCollab based on Project ID and Task ID. 
						- If the retrieved task is categorized as Bug/Issue, create a Bug for it in Bugherd.
						- If the retrieved task is categorized as Support, create a Ticket for it in Freshdesk.
			
			Pre requisite: 
				- Projects and Tasks has to be created in ActiveCollab.
					- Projects should have a custom field to hold the corresponding Bugherd project ID.
					- Tasks should have a custom field to hold the state of the task(whether already added to Bugherd/Freshdesk or not).
						Furthermore at the time of creation a Task in ActiveCollab, it has to be put to one of two custom categories created by the user.
				- Categories are Bug/Issue and Support. User is free to provide any names for the categories but he should retain the category names 
				and send it along with the request for classification.
					- There has to be a active account for the user in Freshdesk.
					- There has to be a active account for the user in Bugherd and a registered project under the account.
			
			Special Note: Request parameter description is as follows. 
				 - activeCollabApiUrl: 		API URL for ActiveCollab account.
				 - activeCollabApiToken: 	API Access token for ActiveCollab account user. User access level should be 'Read and Write' and not 'Read only'.
				 - activeCollabBugherdProjectCustomFieldName: Label/Name of the project level custom field created to hold the Bugherd Project ID.
				 - activeCollabTaskDuplicateTrackerFieldName: Label/Name of the task level custom field created to hold the state of the task (whether already added to Bugherd/Freshdesk or not).
				 - activeCollabBugCategoryName: 	Category name used to categorize a task as bug when creating the task. Please note that ActiveCollab allows user defined categories for tasks. 
				 - activeCollabSupportCategoryName: Category name used to categorize a task as support when creating the task. Please note that ActiveCollab allows user defined categories for tasks. 
				 - activeCollabProjectSelection: Use 'NONE' to choose none of projects by default to process. Use 'ALL' to choose all of the projects coming under the ActiveCollab account to process. 
				 - activeCollabExemptProjects: Array of ActiveCollab project IDs to process or skip.
												If 'NONE' is used for activeCollabProjectSelection, then ONLY the projects specified here will be processed.
												If 'ALL' is used for activeCollabProjectSelection, then the projects specified here will be SKIPPED.
												If the user wants to specify/override Bugherd project ID for an ActiveCollab project, instead of specifying the ID of the Project, use the following format.
												{"id": <ActiveCollab_Project_ID>, "bugherdId": <Bugherd_Project_ID>}
				
				 - activeCollabSkipAddingProjectsToBugherd: 		Array of ActiveCollab project IDs whose tasks should be skipped from getting added to Bugherd.
				 - activeCollabSkipAddingProjectsToFreshDesk: 		Array of ActiveCollab project IDs whose tasks should be skipped from getting added to Freshdesk.
				 - activeCollabSkipAddingCompletedTasksForProjects: Array of ActiveCollab project IDs whose tasks should be skipped from getting added to Bugherd/Freshdesk if they're already completed in ActiveCollab.
				
				 - freshdeskApiUrl - API URL for Freshdesk account.
				 - freshdeskApiKey - API Key for Freshdesk account.
				
				 - bugHerdApiUrl - API URL for BugHerd account. (https://www.bugherd.com)
				 - bugHerdApiKey - API Key for BugHerd account.
				 - bugHerdDefaultProjectId - This will be used as default (Priority 3) Bugherd project ID for all the ActvieCollab Projects if both the following scenarios are encountered:
										- The Bugherd project ID is not specified along with the Activecollab project ID in 'activeCollabExemptProjects' (Priority 1).
										- The Bugherd project ID is not specified in a custom field in Activecollab projects/ The name of the custom field is not specified in 'activeCollabBugherdProjectCustomFieldName' (Priority 2).
			
			Reference: Scenario Guide Document -> Chapter 3.3 -> Step 3  