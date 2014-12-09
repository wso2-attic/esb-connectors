Product: Ebay Business Scenarios

Environment Set-up:

 - Download and initialize ESB 4.8.1 .
 
 - Upload the following connectors to the ESB.
 
			-> ebay-connector-1.0.0
			-> cm-connector-2.0.0
			-> tradegecko-connector-1.0.0	
			-> facebook-connector-1.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            Ebay - https://docs.wso2.com/display/CONNECTORS/Ebay+connector
			Campaign Monitor - https://docs.wso2.com/display/CONNECTORS/Campaign+Monitor+Connector
			Tradegecko - https://docs.wso2.com/display/CONNECTORS/TradeGecko+Connector
			Facebook - https://docs.wso2.com/display/CONNECTORS/Facebook+Connector
			
 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<Ebay_Connector_Home>/ebay-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consists of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy, sequence and the template files which reside in the sub-folder, into the ESB.			
 
 Scenarios in Brief:   

  - Item Initiation

	Case-001 -> 
			Scenario Name: Create Ebay items.
			Description: Creating Ebay items by retrieving product variant details from the TradeGecko.
			Pre requisite: TradeGecko product variant must have description and retail price. 
			Request parameter description is as follows:
				- tradegeckoApiUrl - API URL for TradeGecko.
				- tradegeckoAccessToken - Access token for TradeGecko. 
				- ebayApiUrl - API URL for Ebay.
				- ebayAuthToken - Auth token for Ebay.
				- ebaySiteId - The site id of the ebay.
				- ebayVersion - The ebay request version.
				- ebayAppId - Ebay application id.
				- ebayRouting - The ebay request version.
				- ebayWarningLevel - The Warning Level of response.
				- ebayErrorLanguage - The Error Language of response.
				- items - JSON object which contain item details array(See sample request).
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 1
	
	Case-002 -> 
			Scenario Name: Converting ebay item(s) for promotional sale.
			Description: Converting already existing ebay item(s) as a promotional sale item(s). Then creating a campaign in campaign monitor for promoted item(s) in ebay
						 and creating a facebook wall post on seller's wall with promoted item(s) details in ebay.
			Pre requisite: 
				- EBay item(s) which going to promote must have a valid picture URL. Picture URL must end with a valid image extension. (e.g.: .png, .jpg, etc.)
				- Ebay item(s) promotable period should be within a listing duration of that ebay items.
			Request parameter description is as follows:
				- ebayApiUrl - API URL of the ebay SOAP API.
				- ebayAppId - ebay application id.
				- ebayVersion - ebay version of the payload schema using.
				- ebayRouting - parameter to direct the call to a single namespace server.
				- ebaySiteId - ebay site id that item of interest or listed on.
				- ebayAuthToken - Authentication token for ebay.
				- ebayAction - Action to perform on ebay operations. (e.g.: Add)
				- ebayPromotionalSaleName - ebay promotion name to create.
				- ebayPromotionalSaleStartTime - ebay promotion start time.
				- ebayPromotionalSaleEndTime - ebay promotion end time.
				- ebayPromotionalSaleType - Type of ebay promotional sale. (e.g.: FreeShippingOnly)
				- ebayPromotionalSaleItemIDArray - Array of ebay items which going to convert as a promotional sale.
				- ebayDetailLevel - ebay instruction parameter that define standard subsets of data to return for particular data. (e.g. ReturnAll)
				- cmApiUrl - API URL of the Campaign Monitor.
				- cmAccessToken - Access Token of the Campaign Monitor.
				- cmTemplateId - Id of the pre created campaign template.
				- cmReplyTo - Email address to reply for the campaign.
				- cmSubject - Subject of the campaign.
				- cmName - Unique name for the campaign.
				- cmFromEmail - Campaign sender's email.
				- cmListIds - Id array of the Campaign Monitor subscriber list.
				- cmFromName - Campaign sender's name.
				- cmClientId - Client Id of the Campaign Monitor.
				- fbApiUrl - API URL of Facebook.
				- fbAccessToken - Access Token of the Facebook.
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 2
				
	Case-003 -> 
			Scenario Name: Create Ebay Classified ad listing.
			Description: Creating Ebay Classified ad listing by retrieving product variant details from the TradeGecko.
			Pre requisite: TradeGecko product variant must have description. 
			Request parameter description is as follows:
				- tradegeckoApiUrl - API URL for TradeGecko.
				- tradegeckoAccessToken - Access token for TradeGecko. 
				- ebayApiUrl - API URL for Ebay.
				- ebayAuthToken - Auth token for Ebay.
				- ebaySiteId - The site id of the ebay.
				- ebayVersion - The ebay request version.
				- ebayAppId - Ebay application id.
				- ebayRouting - The ebay request version.
				- ebayWarningLevel - The Warning Level of response.
				- ebayErrorLanguage - The Error Language of response.
				- items - JSON object which contain item details array(See sample request).
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 3
			
	- Process Monitoring

		Case-001 -> 
			Scenario Name: Monitoring Sold Items.
			Description: Updating Tradegecko stock adjustments and adding buyers contact details into Campaign Monitor subscriber list by retrieving running date eBay sold items.
			Pre requisite: Each sold eBay item Id must have a proper mapping with the Tradegecko product variant Id in request item mapping.
			Request parameter description is as follows:
				-ebayApiUrl						- Trading API URL of the eBay.
				-ebayAppId						- Application Id of the eBay.
				-ebayVersion					- eBay API version.
				-ebayRouting					- Routing value of the eBay.
				-ebaySiteId						- Site Id of the eBay.
				-ebayAuthToken					- Auth Token of the eBay.
				-ebayMessageId					- Message Id of the eBay.
				-ebayErrorLanguage				- The Error Language of response.
				-ebayWarningLevel				- The Warning Level of response.
				-itemsMap						- Mapping of ebay item Id with the Tradegecko product variant ID (e.g.: {"eBay item Id": "Tradegecko product variant Id"}). 
				-tradegeckoApiUrl				- API URL of the Tradegecko.
				-tradegeckoAccessToken			- Access Token of the Tradegecko.
				-tradegeckoAdjustmentNumber		- A unique identifier for the stock adjustment. 
				-tradegeckoNotes				- Notes related to the stock adjustment.
				-tradegeckoReason				- The reason for the stock adjustment.
				-tradegeckoStockLocationId		- Location ID for the stock adjustment.
				-cmAccessToken					- Access Token of the Campaign Monitor.
				-cmApiUrl						- API URL of the Campaign Monitor.
				-cmListId						- Id of the Campaign Monitor subscriber list.
			Reference: Scenario Guide Document 	-> Chapter 3.2 -> Step 4.
				
		Case-002 -> 
			Scenario Name: Monitoring Unsold(retrieving active items from eBay which are not sold any quantity yet.) Items.
			Description: Creating a campaign from a template by retrieving items from eBay which are not sold any quantity yet.
			Pre requisite: All the eBay store unsold item(s) must have a valid picture URL. Picture URL must end with a valid image extension (e.g.: .png, .jpg, etc.).
			Request parameter description is as follows:
				-ebayApiUrl			- Trading API URL of the eBay.
				-ebayAppId			- Application Id of the eBay.
				-ebayVersion		- eBay API version.
				-ebayRouting		- Routing value of the eBay.
				-ebaySiteId			- Site Id of the eBay.
				-ebayAuthToken		- Auth Token of the eBay.
				-ebayMessageId      - Message Id of the eBay.
				-ebayErrorLanguage 	- The Error Language of response.
				-ebayWarningLevel	- The Warning Level of response.
				-ebayDetailLevel	- Detail level of the response.
				-cmApiUrl			- API URL of the Campaign Monitor.
				-cmAccessToken		- Access Token of the Campaign Monitor.
				-cmClientId			- Client Id of the Campaign Monitor.
				-cmName				- Name of the campaign.
				-cmSubject			- Subject of the campaign.
				-cmFromName			- Campaign sender's name.
				-cmReplyTo			- Email address to reply for the campaign.
				-cmFromEmail		- Campaign sender's email.
				-cmListIds			- List of subscriber list ids.
				-cmSegmentIds		- List of segment Ids.
				-cmTemplateId		- Id of the template. Follow the instructions to create and retrieve a template Id from Campaign Monitor for this scenario case.
									 
									i). Use the following html code to create a template content and save it as html file(e.g.:template.html). Change the parameter values accordingly.
										{TEMPLATE_TITLE}, {TEMPLATE_HEADING}, and {TEMPLATE_DESCRIPTION}.
									
										
									
										<html>
										  <head><title>{TEMPLATE_TITLE}</title></head>
										  <body>
											<p><singleline>{TEMPLATE_HEADING}</singleline></p>
											<div><multiline>{TEMPLATE_DESCRIPTION}</multiline></div>
											<img id="header-image" editable="true" width="500" />
											<repeater>
											  <layout label="My layout">
												<div class="repeater-item">
												  <p><singleline></singleline></p>
												  <div><multiline></multiline></div>
												  <img editable="true" width="500" />
												</div>
											  </layout>
											</repeater>
											<p><unsubscribe>Unsubscribe</unsubscribe></p>
										  </body>
										</html>
									
									ii) Create a new Campaign Monitor template by importing the template content file that created in step (i).
									
									iii) Retrieve the template id from Campaign  Monitor that created in step (ii).					 
				
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 5.
			