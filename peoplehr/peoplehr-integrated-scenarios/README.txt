Product: PeopleHR Business Scenarios

Environment Setup:

 - Download and initialize ESB 4.8.1 .
 
 - Upload the following connectors to the ESB.
 
			-> peoplehr-connector-1.0.0
			-> zohorecruit-connector-1.0.0
			-> zohopeople-connector-1.0.0	
			-> deputy-connector-1.0.0
			-> xero-connector-1.0.0
			
 - Follow the instructions given in the developer guide of above connectors, and enable the ESB axis configurations accordingly. Developer guide locations of aforementioned connectors are listed below.
 
            PeopleHR - https://docs.wso2.com/display/CONNECTORS/PeopleHR+Connector
			ZohoRecruit - https://docs.wso2.com/display/CONNECTORS/ZohoRecruit+Connector
			ZohoPeople - https://docs.wso2.com/display/CONNECTORS/ZohoPeople+Connector
			Deputy - https://docs.wso2.com/display/CONNECTORS/Deputy+Connector
			Xero - https://docs.wso2.com/display/CONNECTORS/Xero+Connector
			
 - Add the corresponding website security certificates to the ESB for aforementioned connectors. 

 - Add the sequences and templates in the "common" directory (<PeopleHR_Connector_Home>/peoplehr-integrated-scenarios/src/common ), to the ESB.  
 
 - Each scenario folder is consisted of sub-folders named as <Case-xxx>. In order to execute a particular case, upload the corresponding proxy and the template files which reside in the sub-folder, into the ESB.
		  
Scenarios in Brief:   

  - Recruitment

	Case-001 -> 
			Scenario Name: Initiate the Recruitment Process.
			Description: Retrieve hired candidates from ZohoRecruit and use the details to create employees in PeopleHR, ZohoPeople, Deputy and Xero.
						 Activation Emails will be sent to Deputy and ZohoPeople Employees.
			Pre requisite: Following custom fields should be added to Zoho Recruit cadidates(Refer the Note). All the hired candidates should have valid non empty values filled in.
							- Gender(Female or Male only)							
							- Latitude
							- Longitude							   
					
						   Following fields should also have valid non empty values(Make sure to add them as custom fields if not available in your account).
							- First name
							- Last name
							- Email
							- Street
							- City
							- State
							- Zip Code
							- Current Job Title
			Reference: Scenario Guide Document -> Chapter 3.1 -> Step 1
			Note: lastEmployeeIdNumber parameter should contain the largest PeopleHR Employee Id's number without the prefix 'PW' value. Created Employees in PeopleHR and ZohoPeople will have PW prefix followed by a number incremented from the given value.
				  Please follow the instructions in https://www.zoho.com/recruit/helpnew/customization/field-level-customization/fields/add-custom-fields.html to add new custom fields to your Zoho Recruit account.

  - WorkforceManagement			
  
	Case-001 ->
			Scenario Name: Create Absence Record.
			Description: Create a leave (Absence Record) in ZohoPeople and create the same leave in Deputy at the same time for a give Employee.
			Pre requisite: 	Make sure the given Employee is in Actve state in ZohoPeople account.
							Activation is an offline process where Employees are requierd to 'Accpet' the invitation sent to their Email in the 'Recruitment' Scenario.
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 2
	
	Case-002 ->
			Scenario Name: Create Absence Record.
			Description: Fetch all the leaves started on a given month from Deputy and add them as absence records in PeopleHR.
			Reference: Scenario Guide Document -> Chapter 3.2 -> Step 2

  - Payroll			
  
	Case-001 ->
			Scenario Name: Handling Payroll.
			Description: Retrieve salary information for a set of provided employees from PeopleHR and update the salary details for the corresponding employees in Xero.
			Pre requisite: Employee/employees needs to be exist in PeopleHR and in Xero having the same email ID (This is covered in Recruitment scenario Case 001)
						   Salary records for the corresponding employee/employees should be added in PeopleHR.	
						   The corresponding employees' pay schedule needs to be set in Xero.
			Reference: Scenario Guide Document -> Chapter 3.3 -> Step 3 
	
	