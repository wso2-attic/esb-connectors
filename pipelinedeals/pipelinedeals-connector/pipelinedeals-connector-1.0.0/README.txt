Product: Integration tests for WSO2 ESB PipelineDeals connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2.	Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
	If required add the X.509 certificate from https://api.pipelinedeals.com to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
	and wso2carbon.jks located in <PIPELINEDEALS_CONNECTOR_HOME>/pipelinedeals-connector/pipelinedeals-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.
	
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

        <messageFormatter contentType="application/pdf" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
        <messageBuilder contentType="application/pdf" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 5. Make sure that PipelineDeals is specified as a module in ESB Connector Parent pom.
        <module>pipelinedeals/pipelinedeals-connector/pipelinedeals-connector-1.0.0/org.wso2.carbon.connector</module>
		
 6. Create a PipelineDeals trial account and derive the API Token.
	i) 	 Using the URL "https://www.pipelinedeals.com/" create an PipelineDeals free trial account.
	ii)	 Obtain the api key for the created account in 6(i) by following the steps:
		(a) Verify the account created in 6. i) by clicking the email link sent by PipelineDeals.
		(b) Login to the account.
		(c) Navigate to Settings -> Account Settings -> PipelineDeals API -> Provide an email address and enable the API access. Copy and retain the API key.
		(d) 
	
 7. Update the pipelinedeals properties file at location "<PIPELINEDEALS_CONNECTOR_HOME>/pipelinedeals-connector/pipelinedeals-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
																			  
	i) 		apiUrl - Base endpoint URL of the PipelineDeals API. Use https://api.pipelinedeals.com.
	ii)		apiKey - API Key obtained in 6. ii) (c)
	
	iii)	companyNameMandatory - Name of the company for mandatory case.
	iv)		companyNameOptional - Name of the company for optional case.
	v)		companyDescriptionOptional - Description of the company for mandatory case.
	vi)		companyEmailOptional - Email of the company for optional case.
	vii)	companyWebsiteOptional - Website of the company for optional case.
	viii)	companyAddress1Optional - Address 1 of the company for optional case.
	ix)		companyAddress2Optional - Address 2 of the company for optional case.
	x)		companyCityOptional - City of the company for optional case.
	xi)		companyStateOptional - State of the company for optional case.
	xii)	companyPostalCodeOptional - Postal code of the company for optional case.
	xiii)	companyCountryOptional - Country of the company for optional case.
	
	xiv)	companyNameUpdated - Updated name of the company.
	xv)		companyDescriptionUpdated - Updated description of the company.
	xvi)	companyEmailUpdated - Updated email of the company.
	xvii)	companyWebsiteUpdated - Updated website of the company.
	xviii)	companyCountryUpdated - Updated country of the company.
	
	xix)	personFirstNameOptional - First name of the person for optional case.
	xx)		personLastNameOptional - Last name of the person for optional case.
	xxi)	personSummaryOptional - Summary of the person for optional case.
	xxii)	personPositionOptional - Position of the person for optional case.
	xxiii)	personEmailMandatory - Email of the person for mandatory case.
	xxiv)	emailPersonOptional - Email of the person for optional case.
	xxv)	personTypeOptional - Type of the person for optional case - Use 'Contact'.
	
	xxvi)	personFirstNameUpdated - Updated first name of the person.
	xxvii)	personLastNameUpdated - Updated last name of the person.
	xxviii)	personSummaryUpdated - Updated summary of the person.
	xxix)	personPositionUpdated - Updated position of the person.
	xxx)	personEmailUpdated - Updated email of the person.
	xxxi)	personTypeUpdated - Updated type of the person - Use 'Lead'.
	
	xxxii)	dealNameMandatory - Name of the deal for mandatory case.
	xxxiii)	dealNameOptional - Name of the deal for optional case.
	xxxiv)	dealSummaryOptional - Summary of the deal for optional case.
	xxxv)	dealClosedDateOptional - Closed date of the deal for optional case in the following format: yyyy-MM-dd hh:mm:ss +z
	xxxvi)	dealArchivedOptional - Archived status of the deal for optional case. Use either 'true' or 'false'.
	xxxvii)	dealValueOptional - Value of the deal for optional case. Use a integer or decimal number.
	xxxviii)dealValueInCentsOptional - Value of the deal in cents for optional case. Multiply the value specified in 7. xxxvii) by 100 and use here.
	
	xxxix)	optionalDealNameUpdated - Updated name of the deal.
	xL)		optionalDealSummaryUpdated - Updated summary of the deal.
	xLi)	optionalDealIsArchivedUpdated - Updated archived status of the deal. Use the boolean negation of what was used in 7. xxxvi).
	xLii)	optionalDealProbabilityUpdated - Updated probability of the deal. Use any integer value between 0-100 except 70.
	
	xLiii)	calendarEntryNameMandatory - Name of the calendar entry for mandatory case.
	xLiv)	calendarEntryNameOptional - Name of the calendar entry for optional case.
	xLv)	calendarEntryDescriptionOptional - Description of the calendar entry for optional case.
	xLvi)	calendarEntryStartTimeOptional - Start time of the calendar entry for optional case in the following format: yyyy-MM-dd hh:mm:ss +z
	xLvii)	calendarEntryEndTimeOptional - End time of the calendar entry for optional case in the following format: yyyy-MM-dd hh:mm:ss +z
	xLviii)	calendarEntryIsActiveOptional - Active status of the calendar entry for optional case.
	
	xLix)	calendarEntryNameUpdated - Updated name of the calendar entry.
	L)		calendarEntryDescriptionUpdated - Updated description of the calendar entry.
	Li)		calendarEntryDueDateUpdated - Updated due date of the calendar entry in the following format: yyyy-MM-dd
	
	Lii)	documentTitleOptional - Title of the document for optional case.
	Liii)	documentUrlOptional - - URL of the document for optional case. Use a valid publicly accessible URL of a web file.
				E.g. http://img3.wikia.nocookie.net/__cb20121202053830/villains/images/1/1a/Lvo1gzjph7qd81tpvm26.jpg
	
	- Integration test suite can be executed multiple times without changing any of the properties except 7. ii) which requires to be a valid key belonging to an active account.
	
 8. Navigate to "{ESB_Connector_Home}/" and run the following command.
         $ mvn clean install

		