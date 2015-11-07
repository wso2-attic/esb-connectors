Product: Integration tests for WSO2 ESB Cliniko connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0
 - Java 1.7
 
STEPS:

 1. Download ESB 4.9.0 by following the URL: http://wso2.com/products/enterprise-service-bus/#

 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
    If required add the X.509 certificate from https://api.cliniko.com to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
    and wso2carbon.jks located in <CLINIKO_CONNECTOR_HOME>/cliniko-connector/cliniko-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.

 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
      <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
      <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

    
 4. Create a Cliniko trial account and derive the API Key and Secret.
   i)    Using the URL "https://www.cliniko.com/#sign-up-form" create a Cliniko trial account.
   ii)   Login to the created Cliniko account and go to 'My Info' section in the dashboard to obtain the API key.
   iii)  Create at least two practitioners (active) providing values for first name, last name and title.
   iv)   Create at least two appointment types providing values for name, description, duration and color.
         All appointment types in the account needs to be associated with all the practitioners created in the account.
   v)    Create at least two products providing values for name, cost price, item code and stock level and notes.
   vi)   There has to be at least two appointments created in the Cliniko account which are cancelled.
   vii)  Do not create any additional businesses in the account. The business that is created at the time of account creation is sufficient.
   viii) Create at least two invoices providing values for net amount, invoice to name and number.

 5. Update the Cliniko properties file at location "<CLINIKO_CONNECTOR_HOME>/cliniko-connector/cliniko-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)        apiUrl                          - Use https://api.cliniko.com.
   ii)       apiKey                          - Use the API Key obtained under Step 4 ii).
   iii)      mandatoryAppointmentEndTime     - End time of the appointment for createAppointment mandatory case. Provide a date-time which is greater than the date-time provided for 'mandatoryAppointmentStartTime'.
   iv)       mandatoryAppointmentStartTime   - Start time of the appointment for createAppointment mandatory case. Use a future date-time.
   v)        appointmentNotes                - Note of the appointment for createAppointment mandatory case.
   vi)       appointmentEndTimeUpdated       - End time of the appointment for updateAppointment optional case. Provide a date-time which is greater than the date-time provided for 'appointmentStartTimeUpdated'.
   vii)      appointmentStartTimeUpdated     - Start time of the appointment for updateAppointment optional case. Use a future date-time.
   viii)     appointmentNoteUpdates          - Notes of the appointment for updateAppointment optional case.
   ix)       availableFromDate               - Start date to search for available time slots. Use a future date.
   x)        availableToDate                 - End date to search for available time slots. Provide a date which is greater than, but within 7 days of the date provided for 'availableFromDate'.
   xi)       medicalAlertNameUpdated         - Name to update the medical alert with.
   xii)      optionalPatientAddressLine1     - Address Line 1 of the patient for createPatient optional case.
   xiii)     optionalPatientAddressLine2     - Address Line 2 of the patient for createPatient optional case.
   xiv)      optionalPatientAddressLine3     - Address Line 3 of the patient for createPatient optional case.
   xv)       optionalPatientCity             - City of the patient for createPatient optional case. Give a valid city name.
   xvi)      optionalPatientCountry          - Country of the patient for createPatient optional case. Give a valid country name.
   xvii)     optionalPatientDateOfBirth      - Date of birth of the patient for createPatient optional case. A date in the past format as 'yyyy-MM-dd'.
   xiii)     optionalPatientEmail            - Email of the patient for createPatient optional case. Email needs to be in the valid format '<username>@<sub-domain>.<domain>'.
   xix)      mandatoryPatientFirstName       - First name of the patient for createPatient mandatory case.
   xx)       optionalPatientFirstName        - First name of the patient for createPatient optional case.
   xxi)      optionalPatientGender           - Gender of the patient for createPatient optional case. Use 'Male' or 'Female'.
   xxii)     mandatoryPatientLastName        - Last name of the patient for createPatient mandatory case.
   xxiii)    optionalPatientLastName         - Last name of the patient for createPatient optional case.
   xxiv)     medicalAlertName                - Name of the medical alert.
   
   Note: When providing date-time values for the following properties, use the format 'yyyy-MM-ddThh:mm:ssZ'
         mandatoryAppointmentEndTime, mandatoryAppointmentStartTime, appointmentEndTimeUpdated, appointmentStartTimeUpdated
         
         When providing date-time values for the following properties, use the format 'yyyy-MM-dd'
         availableFromDate, availableToDate

 6. Make sure that cliniko is specified as a module in ESB Connector parent pom.xml.
        <module>cliniko/cliniko-connector/cliniko-connector-1.0.0/org.wso2.carbon.connector</module> 

 7. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install