Product: Prodpad Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-BETA-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
			-> prodpad-connector-1.0.0
			-> pivotaltracker-connector-1.0.0
			-> jotform-connector-1.0.0
			-> zendesk-connector-1.0.0
			-> mandrill-connector-1.0.0
			
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.
			
			ProdPad - https://docs.wso2.com/display/CONNECTORS/ProdPad+Connector
			PivotalTracker - https://docs.wso2.com/display/CONNECTORS/PivotalTracker+Connector
			JotForm  - https://docs.wso2.com/display/CONNECTORS/JotForm+Connector
			Zendesk  - https://docs.wso2.com/display/CONNECTORS/Zendesk+Connector
			Mandrill - https://docs.wso2.com/display/CONNECTORS/Mandrill+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<PRODPAD_CONNECTOR_HOME>/prodpad-integrated-scenarios/src/common), to the ESB that are listed as below.
			- sequences - faultHandlerSeq.xml
			- templates - responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.
 
 - Pre-requisite:	(a)The 'Suggestions Form' should be created in JotForm and the clients should be aware of this form to submit any product related suggestions.
					(b)The company's products should be available in ProdPad, PivotalTracker and JotForm.
					
 01. Idea Initiation
 
	[i] Case -001
		- Purpose:  (a) Retrieve flagged form submissions from Jotform API and create them as ideas in ProdPad API. Flag will be removed after creating the idea.
		- Files:	(a)	Proxy - <PRODPAD_CONNECTOR_HOME>\prodpad-integrated-scenarios\src\scenarios\Idea Initiation\Case-001\proxy\prodpad_ideaInitiationFromJotform.xml
		- Request Parameters:   (a) jotform.formId - The ID of the form through which the users submit their suggestions.
								(b) jotform.titleText - The name of a field in the form which is used to provide the title of the suggestion.
								(c) jotform.productText - The name of a field in the form which is used to provide the product name.
								(d) jotform.usernameText - The name of a field in the form which is used to provide the username.
								(e) jotform.suggestionText - The name of a field in the form which is used to provide a suggestion.
								(f) jotform.emailText - The name of a field in the form which is used to provide the user's e-mail address.
								(g) prodpad.userId - The user ID of the person who creates the idea.			
		- Prerequisite:	(a)	The apiKey which is used for Jotform authentication should be a apiKey which has full access to the api.	
	
	[ii] Case -002
		- Purpose:  (a) Retrieve tickets tagged as 'suggestions' from Zendesk API and create them as ideas in ProdPad API. A tag named 'idea_created' will be attached to the ticket after creating the idea.
		- Files:	(a)	Proxy - <PRODPAD_CONNECTOR_HOME>\prodpad-integrated-scenarios\src\scenarios\Idea Initiation\Case-002\proxy\prodpad_ideaInitiationFromZendesk.xml
		- Request Parameters:   (a) prodpad.userId - The user ID of the person who creates the idea.
		- Prerequisite:	(a)	Zendesk ticket subject should be given in following format: [productName]-ideaTitle
		
 02. Idea Management		
 
	[i] Case -001
		- Purpose:  (a) Retrieve comments of ideas which is associated to a selected product (or all products) from ProdPad API and add them as comments to the relevant ticket in Zendesk API if the idea is originated from Zendesk. If the idea is originated from Jotform, send these comments to the client using Mandrill API.
		- Files:	(a)	Proxy - <PRODPAD_CONNECTOR_HOME>\prodpad-integrated-scenarios\src\scenarios\Idea Management\Case-001\proxy\prodpad_retrieveIdeaCommentsAndSend.xml.xml
		- Request Parameters:   (a) prodpad.product - The name of the product for which, the idea comments need to be retrieved(if this is empty, it will retrieve idea comments for all products).
								(b) mandrill.fromEmail - The e-mail address from which the email is sent.
								(c) mandrill.fromName - The e-mail sender's name that will be added to the e-mail body.
					
	[ii] Case -002
		- Purpose:  (a) Retrieve ideas of which the status has been changed within the current day from ProdPad API and update the corresponding status of tickets in Zendesk API if the idea is created from Zendesk API. If the idea is created from Jotform API send a notification mail of the status update through Mandrill API to the user who submitted the suggestion .
		- Files:	(a)	Proxy - <PRODPAD_CONNECTOR_HOME>\prodpad-integrated-scenarios\src\scenarios\Idea Management\Case-002\proxy\prodpad_updateStatusChanges.xml
		- Request Parameters:   (a) mandrill.fromEmail - The e-mail address from which the email is sent.
								(b) mandrill.fromName - The e-mail sender's name that will be added to the email body.

 03. Product Management		
 
	[i] Case -001
		- Purpose:  (a) Retrieve user stories of ideas which are in "Queued for Dev" status from ProdPad API, and create stories in the PivotalTracker API for the corresponding project(product in ProdPad) and label them using the associated idea ID of the ProdPad API. Update the status of the ideas which were retrieved, to "In Development" in ProdPad API.
		- Files:	(a)	Proxy - <PRODPAD_CONNECTOR_HOME>\prodpad-integrated-scenarios\src\scenarios\Product Management\Case-001\proxy\prodpad_createStoriesInPivotaltracker.xml
		- Request Parameters:   (a) prodpad.inDevelopmentStatusId - The ID of the status "In Development".
					
	[ii] Case -002
		- Purpose:  (a) Retrieve stories by product in ProdPad API(project in PivotalTraker API) and check if all the related stories are in "Finished" or "Delivered" state in PivotalTracker API, and then update the idea status in ProdPad API as "Released".
		- Files:	(a)	Proxy - <PRODPAD_CONNECTOR_HOME>\prodpad-integrated-scenarios\src\scenarios\Product Management\Case-002\proxy\prodpad_releaseIdeas.xml
		- Request Parameters:   (a) prodpad.productName - Name of the product in the Prodpad API.
								(b) prodpad.releasedStatusId - The ID of the status "Released".
					
					