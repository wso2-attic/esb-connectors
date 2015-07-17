Product: Integration tests for WSO2 ESB ActiveCollab connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 14.04
    - WSO2 ESB 4.9.0
 
Steps to follow in setting integration test.
 1.  Download ESB 4.9.0 from official website.
 2.  Deploy relevant patches, if applicable.

STEPS:
		
 1. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
	
	Message Formatters :-
		
	<messageFormatter contentType="application/json"
							  class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
							  
	<messageFormatter contentType="text/html"                             
					  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
					  
	<messageBuilder contentType="application/json"
					  class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

	<messageBuilder contentType="text/html"                                
					  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
	
 2. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{activecollab_CONNECTOR_HOME}/activecollab-connector/activecollab-connector-1.0.0/org.wso2.carbon.connector/repository/".
	
 3. Create an activecollab trial account and derive the API key:
	i) 	 Using the URL "https://www.activecollab.com/" create an activecollab Free Trial account.
	ii)  Login to the created activecollab account and derive the API Key by selecting the user account that has been created >> Click on Options >> Select API Subscription >> Create a new Subscription (make sure that the 'Read Only' radio button is set as 'No') >> click the API subscription details icon and get the API URL and the api token. 
	iii) Add a project Role following the below steps
		 a)Select Administration and click on Administration.
		 b)Under Projects select 'Project Roles'.
		 c)Click on New Role and enter a name for the role >> select the appropriate permissions and click 'Add Role'
		 
    iv)  Create at least two users and keep the user ids for further reference.
	
 4. Update the activecollab properties file at location "{activecollab_CONNECTOR_HOME}/activecollab-connector/activecollab-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i) 		apiUrl - Use the API URL that is obtained under step 3 ii).
	ii) 	apiToken - API token that is obtained under Step 3 ii).
	iii) 	companyId - Use a valid company ID.
	iv) 	projectNameMandatory - Use a valid string as the project name.
	v) 		projectNameOptional - Use a valid string as the project name.
	vi)     emailMandatory - Use a valid email address.
	vii) 	emailOptional - Use a valid email address.
	viii) 	password - Use a valid string as the password.
	ix)		firstName - Use a valid string as the first name of the user.
	x)		lastName - Use a valid string as the last name of the user. 
	xi)		userType - Use 'Client'.
	xii)	taskName - Use a valid string as the task name.
	xiii)	taskDesc - Use a valid string as the task description.
	xiv)	taskVisibility - Use '1' or '0'.
	xv)		taskPriority - Use a priority value that can have one of five integer values, ranging from -2. 
	xvi)	updatedTaskName - Use a valid string as a new name for the task to be updated.
	xvii)	updatedTaskDesc - Use a valid string as a new description for the task to be updated.
	xviii)	milestoneName - Use a valid string as the milestone name.
	xix)	milestoneNameOptional - Use a valid string as the milestone name.
	xx)		startOn - Use a valid date in the format of YYYY-MM-DD.
	xxi)	assignUserIdMandatory - Use a valid user Id that is created in Step 3 iv).
	xxii)	assignUserIdOptional - Use a valid user Id that is created in Step 3 iv).
	xxiii)	discussionName - Use a valid string as the name of the discussion.
	xxiv)	discussionBody - Use a valid string as the description of the discussion.
	xxv)    discussionVisibility - Use either 0 or 1 (0 marks private and 1 stands for normal visibility). 

	Properties vi) and vii) needs to be changed before running the integration test each time.
		
 5. Make sure that the activecollab connector is set as a module in esb-connectors parent pom.
               <module>activecollab/activecollab-connector/activecollab-connector-1.0.0</module>

 6. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
               $ mvn clean install

 NOTE : 
	  -activecollab Free trial account is only valid for 30 days.
	  
