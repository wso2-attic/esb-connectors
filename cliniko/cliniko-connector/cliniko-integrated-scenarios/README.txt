Product: Cliniko Integrated Scenarios

Environment Set-up:

 - Download and initialize ESB 4.9.0-BETA-SNAPSHOT with patches.
 
 - Upload the following connectors to the ESB.
 
         -> cliniko-connector-1.0.0
         -> nexmo-connector-1.0.0
         -> billomat-connector-1.0.0
         -> formstack-connector-1.0.0
         -> googlecalendar-connector-1.0.0
         -> mandrill-connector-1.0.0
         -> livechat-connector-1.0.0
         -> eventbrite-connector-1.0.0
			
 - Follow the instructions given in the developer guide of the above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of the aforementioned connectors are listed below.

         Cliniko           - https://docs.wso2.com/display/CONNECTORS/Cliniko+Connector
         Nexmo             - https://docs.wso2.com/display/CONNECTORS/Nexmo+Connector
         Billomat          - https://docs.wso2.com/display/CONNECTORS/SalesBinder+Connector
         Formstack         - https://docs.wso2.com/display/CONNECTORS/Formstack+Connector
         Google Calendar   - https://docs.wso2.com/display/CONNECTORS/Google+Calendar+Connector
         Mandrill          - https://docs.wso2.com/display/CONNECTORS/Mailchimp+Connector
         LiveChat          - https://docs.wso2.com/display/CONNECTORS/LiveChat+Connector
         Eventbrite        - https://docs.wso2.com/display/CONNECTORS/Eventbrite+Connector

 - If required, add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<CLINIKO_CONNECTOR_HOME>/cliniko-integrated-scenarios/src/common), to the ESB that are listed as below.
         - sequences - cliniko_getSettingsSeq.xml
                       faultHandlerSeq.xml
                       removeResponseHeaders.xml
   
         - templates - responseHandlerTemplate.xml
 
 -  Prerequisites for the Scenario.
      (a)   Add the following registry entries with valid values to the ESB (on which the scenarios would be executed).
   
            - /_system/governance/connectors/Cliniko/apiKey
            - /_system/governance/connectors/Cliniko/apiUrl
          
            - /_system/governance/connectors/Nexmo/apiUrl
            - /_system/governance/connectors/Nexmo/apiKey
            - /_system/governance/connectors/Nexmo/apiSecret
            
            - /_system/governance/connectors/Billomat/apiUrl
            - /_system/governance/connectors/Billomat/apiKey
            
            - /_system/governance/connectors/Formstack/apiUrl
            - /_system/governance/connectors/Formstack/accessToken
            
            - /_system/governance/connectors/Googlecalendar/apiUrl
            - /_system/governance/connectors/Googlecalendar/accessToken
            
            - /_system/governance/connectors/Mandrill/apiKey
            - /_system/governance/connectors/Mandrill/apiUrl
            
            - /_system/governance/connectors/Livechat/apiKey
            - /_system/governance/connectors/Livechat/apiUrl
            - /_system/governance/connectors/Livechat/login
            
            - /_system/governance/connectors/Eventbrite/apiUrl
            - /_system/governance/connectors/Eventbrite/accessToken
 
 - Each scenario folder consists of sub-folders named as <Case-xxx>. Before executing each case, please read through the corresponding Case section in this document.			
 
 01. Medical Campaigns
 
   [i] Case -001
      - Purpose : (a)  Get the event details and the ticket details for a specific event in Eventbrite.
                        (i)   Create the event attendees as patients in Cliniko if don't exist.
                        (ii)  Create an appointment for the patient in cliniko under the practitioner given in the ticket related to the attendee.
                        (iii) Create an event in the common googlecalendar and add the particular practitioner as an attendee for the created event.
                  
      - Files:    (a)   Proxy - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Medical Campaigns\Case-001\proxy\cliniko_retrieveEventsAndCreateAppointments.xml
                  (b)   Templates - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Medical Campaigns\Case-001\template\createAppointments.xml
                  
      - Request Parameters:   (a) cliniko.appointmentType    -  The name of the appointment type to which the appointments will be created. 
                              (b) googlecalendar.calendarId  -  The ID of the common google calendar.
                              (c) shared.timeZone            -  The time zone of both the APIs 'Cliniko' and 'GoogleCalendar'.(e.g. '+05:30').
                              (c) eventBrite.eventId         -  The ID of the event to which the appointments will be created.
      
      - Prerequisites :   (a)   Eventbrite -   
                              (a)   The ticket class description should contain the practitioner ID in Cliniko for whom the appointment should be created. Please adhere to the following format   
                                       e.g. Red Eye (Practitioner ID:41213)

                         
  02. Appointment Creation
 
   [i] Case -001
      - Purpose : (a)  Retrieve daily tickets from Livechat which are tagged as 'Appointments'.
                        (i)   Create the ticket requester as a patient in Cliniko if doesn't exist.
                        (ii)  Create an appointment for the patient in cliniko under the practitioner given in the Livechat ticket.
                        (iii) Create an event in the common googlecalendar and add the particular practitioner as an attendee for the created event.
                  
      - Files   : (a)   Proxy - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Appointment Creation\Case-001\proxy\cliniko_retrieveTicketsAndCreateAppointments.xml
                  (b)   Templates - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Appointment Creation\Case-001\template\createAppointments.xml
                  
      - Request Parameters:   (a) cliniko.appointmentType    -  The name of the appointment type to which the appointments will be created. 
                              (b) googlecalendar.calendarId  -  The ID of the common google calendar.
                              (c) shared.timeZone            -  The time zone of both the APIs 'Cliniko' and 'GoogleCalendar'.(e.g. '+05:30').
      
      - Prerequisites:     (a)  Livechat -   
                                 (a)   A tag named 'Appointments'should be created in Livechat.
                                 (b)   The ticket subject should contain the practitioner ID of Cliniko for whom the appointment should be created followed by the subject. Please adhere to the following format   
                                       e.g. Red Eye (Practitioner ID:41213)
                                 (c)   The requester name of the ticket should be given in the format where the first name and the last name is separated by a space.
                                       e.g. Elena Gilbert
                           
                             (b) The time zones of the Cliniko API and GoogleCalendar API should be the same.

  03.  Service Handling

	[i] Case -001
		- Purpose : (a)   Retrieve daily invoices from the Cliniko API.
                           (i)   Create an completed invoice in Billomat API and create the patient as customer in Billomat if doesn't exist.
                           (ii)  Create and send a survey via formstack API to the patient to get feedbacks.
                           
		- Files:    (a)   Proxy -     <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Service Handling\Case-001\proxy\cliniko_createInvoicesAndSurveys.xml
                    
                  (b)   Sequences - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Service Handling\Case-001\Sequences\billomat_createCompletedInvoiceSeq.xml
                                    <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Service Handling\Case-001\Sequences\formstack_createAndSendSurveySeq.xml
                    
                  (c)   Templates - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Service Handling\Case-001\template\formstack_updateFieldContent.xml
		
		- Request Parameters:   (a) formstack.templateFormId  -  ID of the form that would be used as a template to create forms for each patient.
                              (b) formstack.invoiceIdLabel  -  Label name of the field in which the invoice ID of Cliniko API would be saved.
                              (c) formstack.adminEmailLabel -  Label name of the field in which the administrator's email of Cliniko API would be saved.
                              (d) mandrill.fromName         -  Name of the company/person to be used as the sender name when sending the surveys.
                              (e) mandrill.fromEmail        -  Email of the company/person to be used as the sender's email when sending the surveys.
                              
      - Prerequisites:  (a)   Formstack - 
                              (a)   A form should be created in Formstack which is used as template ID to clone the form with following fields.
                                       - Invoice Id (Short Answer field type - Hidden)
                                             Make sure to make this field one before the last out of all the fields.
                                       - Cliniko Admin Email (Short Answer field type - Hidden)
                                             Make sure to make this field the last out of all the fields.

                              (b)   Include three or more multiple choice questions to get customer feedback  (Radio button type)
                        (b)   Cliniko - 
                              (a)   When creating invoices in Cliniko API make sure to add a note to the invoice which indicates for what the invoice has been made.

   [ii] Case -002
        - Purpose :  (a)   Gather responses of surveys in Formstack and send them to the administrator of Cliniko API.
   
        - Files   :  (a)   Proxy - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Service Handling\Case-002\proxy\cliniko_sendSurveyResponseToAdmin.xml
   
        - Request Parameters:   (a) formstack.questions -  Array of objects each with the index of the question in the template form (in Formstack) and the label of the question.
                                                         Please note that the values given for questions will be included in the mail sent to the administrator.
                                                   E.g.  [
                                                            {"index":4, "question":"How satisfied are you with the quality of the products that you have purchased?"},
                                                            {"index":5, "question":"How satisfied are you with speed and the quality of the delivery?"},
                                                            {"index":6, "question":"How satisfied are you with doing business with our company?"},
                                                            {"index":7, "question":"How likely are you to recommend our company to others?"}
                                                         ]
                                 
                                (b) mandrill.fromName   -  Name of the company/person to be used as the sender name when sending the survey response.
                                (c) mandrill.fromEmail  -  Email of the company/person to be used as the sender email when sending the survey response.
                        
        - Special Notes: (a) Surveys should be completed in order to be considered in the case.
                         (b) When multiple submissions are made for a single survey, the most recent submission would be considered.
                         (c) Surveys without submission which are older than 14 days will be deleted upon execution of the case.

 04. Notifications   
 
      [i] Case -001
            - Purpose : (a) Send birthday wishes to the patients in Cliniko API using Nexmo API .
   
            - Files:    (a) Proxy - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Notifications\Case-001\proxy\cliniko_sendBirthdayWishes.xml
   
            - Special notes :  (a)   The patient should provide the mobile number along with the country code in order to get a success response. (example :94710425557)           

      [ii] Case -002
            - Purpose : (a) Notify appointment cancellations to the patients in Cliniko API using Nexmo API.
                        (b) Create medical alerts for each appointment cancellation in Cliniko API.

            - Files:    (a)   Proxy - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Notifications\Case-002\proxy\cliniko_notifyAppointmentCancellation.xml

            - Prerequisites:  (a)   The mobile number of the patient should be provided with the country code. (example :94710425557)              

      [iii] Case -003
               - Purpose : (a)   Retrieve medical alerts in the Cliniko API and notify them to the related patient if it is to be notified via Nexmo API.
   
               - Files   :	(a)   Proxy - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Notifications\Case-003\proxy\cliniko_retrieveMedicalAlertsAndNotifyPatients.xml
   
               - Special notes : (a)  The medical alerts in the following format are considered to be notified to the patients. 
                                        - 'Notify to patient:Collect the medicine'
                                       
               - Prerequisites:  (a)   The mobile number of the patient should be provided with the country code. (example :94710425557) 
  
        [iv] Case -004
                - Purpose :   (a)   Retrieve all the product with lower stock level and send a report of those to the cliniko administrator via Mandrill API.
      
                - Files   :   (a)   Proxy - <CLINIKO_CONNECTOR_HOME>\cliniko-integrated-scenarios\src\scenarios\Notifications\Case-004\proxy\cliniko_notifyStockLevels.xml 
      
                - Request Parameters  :   (a) mandrill.fromName         -  Name of the company/person to be used as the sender name when sending the stock report.
                                          (b) mandrill.fromEmail        -  Email of the company/person to be used as the sender email when sending the report.
  
                - Special notes :  (a) Make sure to give supplier's details along with the reorder level when creating the products and it should be in the following format.
                                          - 'AWS (Pvt)Ltd-yasasitest@gmail.com(Reorder Level:200)'
