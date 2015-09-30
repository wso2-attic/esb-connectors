Product: HubSpot Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-BETA-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
         -> hubspot-connector-1.0.0
         -> activecampaign-connector-1.0.0
         -> googlecalendar-1.0.0
         -> contactform-connector-1.0.0
         -> tsheets-connector-1.1.0
         -> billiving-connector-1.0.0
         
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.
         
         HubSpot - https://docs.wso2.com/display/CONNECTORS/Hubspot+Connector
         ActiveCampaign - https://docs.wso2.com/display/CONNECTORS/ActiveCampaign+Connector
         GooglrCalendar  - https://docs.wso2.com/display/CONNECTORS/Google+Calendar+Connector
         123ContactForm  - https://docs.wso2.com/display/CONNECTORS/123ContactForm
         TSheets - https://docs.wso2.com/display/CONNECTORS/TSheets+Connector
         Billiving - https://docs.wso2.com/display/CONNECTORS/Billiving+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<HUBSPOT_CONNECTOR_HOME>/hubspot-integrated-scenarios/src/common), to the ESB that are listed as below.
         - sequences - faultHandlerSeq.xml
         - templates - responseHandlerTemplate.xml
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.
 
 - Pre-requisite: (a) The 'Client Details Form' should be created in 123ContactForm API and the people who are interest should be aware of this form to submit their details.
                  (b) The 'Client Details Form' in 123ContactForm API should have 'Text Box' fields with following names:
                     - First Name
                     - Last Name
                     - Company
                     - Email
               
               
 01.Deal Creation
 
   [i] Case -001
      - Purpose:  (a) Create and send campaigns using the ActiveCampaign API and create a contact list associated to the campaign in ActiveCampaign API.
      - Files:    (a) Proxy - <HUBSPOT_CONNECTOR_HOME>\hubspot-integrated-scenarios\src\scenarios\Deal Creation\Case-001\proxy\hubSpot_createCampaignAndMailingList.xml        
      - Request Parameters:   (a) activeCampaign.campaign.format - Foramt of the campaign message.
                              (b) activeCampaign.campaign.fromEmail - Email address which the campaign message is sent from.
                              (c) activeCampaign.campaign.fromName - Name which the campaign message is sent from.
                              (d) activeCampaign.campaign.replyTo - Email address for the campaign message to reply.
                              (e) activeCampaign.campaign.listId - Mailing list id which the campaign is sent to. 
                              (f) activeCampaign.campaign.type - Type of the campaign. 
                              (g) activeCampaign.campaign.name - Name of the campaign. 
                              (h) activeCampaign.campaign.subject - Subject for the campaign message.
                              (i) activeCampaign.campaign.emailTextContent - Text content for the campaign message.
                              (j) activeCampaign.campaign.priority - Priority of the campaign message.
                              (k) activeCampaign.campaign.charset - Charset of the campaign message.
                              (l) activeCampaign.campaign.encoding - Encoding used to the campaign message.
                              (m) activeCampaign.campaign.htmlConstructor - HTML version of the campaign message.
                              (n) activeCampaign.campaign.html - HTML version content of the campaign message.
                              (o) activeCampaign.campaign.htmlFetch - Url where to fetch the body for the campaign message.
                              (p) activeCampaign.campaign.htmlFetchWhen - When to fetch the html body.
                              (q) activeCampaign.campaign.textFetch - Url where to fetch the body for the campaign message.
                              (r) activeCampaign.campaign.textFetchWhen - When to fetch the text body.
                              (s) activeCampaign.campaign.isPublic - Visibility of the campaign.
                              (t) activeCampaign.campaign.trackLinks - Types of the links to track.
                              (u) activeCampaign.mailingList.toName - To name for the mailing list.
                              (v) activeCampaign.mailingList.senderAddress - Address of the sender.
                              (w) activeCampaign.mailingList.senderZip - Zip of the sender.
                              (x) activeCampaign.mailingList.subscriptionNotifyList - Email addresses for notify new subscriptions for the mailing list.
                              (y) activeCampaign.mailingList.senderCountry - Country of the sender.
                              (z) activeCampaign.mailingList.senderName - Name of teh sender.
                              (aa) activeCampaign.mailingList.unsubscriptionNotifyList - Email addresses for notify unsubscriptions for the mailing list.
                              (ab) activeCampaign.mailingList.senderCity - City of the sender.
                              (ac) activeCampaign.mailingList.senderUrl - Url of teh sender.
                              (ad) activeCampaign.mailingList.carbonCopyList - Email addresses to send a copy of all mailings to upon send.
                              (ae) activeCampaign.mailingList.bounceId - Bounce management accounts.
                              (af) activeCampaign.mailingList.senderReminder - Reminder for the mailing list.
                              (ag) activeCampaign.mailingList.stringId - Url safe list name for the mailing list.
   
   [ii]Case -002
      - Purpose:  (a)   Retrieve campaign clickers from the ActiveCampaign API and form submissions from the 123ContactForm API and add them as contacts to the campaign contacts list(contact list named with the campaign name) in the ActiveCampaign API.  
      - Files:    (a)   Proxy - <HUBSPOT_CONNECTOR_HOME>\hubspot-integrated-scenarios\src\scenarios\Deal Creation\Case-002\proxy\hubspot_addContactsToContactList.xml      
      - Request Parameters:   (a) activeCampaign.campaignName - The name of the campaign.
                              (b) contactForm.formId - The id of the form which will be used by the people who are interest, to submit their details. 
   
   [iii] Case -003
      - Purpose:  (a) Retrieve the deals in stage "Contacted" from the ActiveCampaign API and create the respective company, contact and deal in the Hubspot API.
      - Files:    (a) Proxy - <HUBSPOT_CONNECTOR_HOME>\hubspot-integrated-scenarios\src\scenarios\Deal Creation\Case-003\proxy\hubSpot_createDealsCompaniesAndContacts.xml  
      - Prerequisite:   (a) Create a custom property for deal as 'Active Campaign Id' with the field type 'Single-line text' in Hubspot API.
                        (b) Create a deal stage in ActiveCampaing API named "Contacted".
      
      
 02.Event Handling      
 
   [i] Case -001
      - Purpose:  (a) Retrieve deals which are in the 'Presentation Scheduled' stage from the Hubspot API and create an engagement of type ‘Meeting’ in the Hubspot API and add it to Google Calendar API with the relevant contact as an attendee.
      - Files:    (a) Proxy - <HUBSPOT_CONNECTOR_HOME>\hubspot-integrated-scenarios\src\scenarios\Event Handling\Case-001\proxy\hubSpot_eventHandling.xml   
      - Request Parameters:   (a) googleCalendar.calendarId - The email address of the person who is creating the event.
                              (b) googleCalendar.timeZone - The timezone which should be used to create the event.
      - Prerequisite:   (a) Create custom properties for deals as follows in Hubspot API:
                           -  'Meeting Start Date' and 'Meeting End Date' with the field type 'Date picker'.
                           -  'Meeting' field with the field type 'Radio select' and with values 'Created' and 'Not Created'.
                           -  'Meeting Start Time(HH:mm:ss)' and 'Meeting End Time(HH:mm:ss)' with the field type 'Single-line text'.
                        (b) Give values for the custom properties as follows:
                           -  'Meeting Start Date' and 'Meeting End Date' values should have same value.
                           -  'Meeting Start Time(HH:mm:ss)' and 'Meeting End Time(HH:mm:ss)' custom property values should follow the format which is given in the brackets.
                        (c) The GoogleCalendar access token should be taken by logging into the email account given in the googleCalendar.calenderId request parameter.
      
 03.Deal Completion     
 
   [i] Case -001
      - Purpose:  (a) Retrieve deals which are in “Won” stage from the Hubspot API and;
                       Create the client in the Billiving API, if they do not exist.
                       Create a project (deal) in the TSheets API (The parent job code will be the client, which can have multiple child job codes as deals. These deals will have time entries.)
                       Update the deal stage as “Won” in the ActiveCampaign API.
      - Files:    (a) Proxy - <HUBSPOT_CONNECTOR_HOME>\hubspot-integrated-scenarios\src\scenarios\Deal Completion\Case-001\proxy\hubSpot_createContactsAndProjectsForWonDeals.xml  
      - Prerequisite:  (a) Create custom properties for deals as follows in Hubspot API:
                           - 'TSheet Job Code' with the field type 'Single-line text'.
                           - 'Hourly Rate' with the field type 'Single-line text'.
                       (b) Create custom properties for contacts as follows in Hubspot API:
                           - 'TSheets Job Code' with the field type 'Single-line text'.
                           - 'Biliving Client ID' with the field type 'Single-line text'    
      
               
   [ii] Case -002
      - Purpose:   (a) Retrieve time entries for a particular period from the TSheets API and create an invoice in the Billiving API for book keeping purposes.
      - Files:     (a) Proxy - <HUBSPOT_CONNECTOR_HOME>\hubspot-integrated-scenarios\src\scenarios\Deal Completion\Case-002\proxy\hubSpot_createInvoicesForTimeEntries.xml
      - Request Parameters(mandatory): (a) tSheets.startDate - Start date in YYYY-MM-dd fromat to retrieve time sheets.
                                       (b) tSheets.endDate - End date in YYYY-MM-dd fromat to retrieve time sheets.
               
               