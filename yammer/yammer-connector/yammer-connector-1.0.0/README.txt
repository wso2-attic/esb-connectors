Product: Integration tests for WSO2 ESB Yammer connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-ALPHA

Steps to follow in setting integration test.

 1. Download WSO2 ESB 4.9.0-ALPHA from official website.

 2. The ESB should be configured as below;
	i)  Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).

		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

		<messageFormatter contentType="application/octet-stream" class="org.apache.axis2.format.BinaryFormatter"/>

		<messageBuilder contentType="application/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

		<messageFormatter contentType="application/xml" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="application/xml" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 3. Compress modified ESB as wso2esb-4.9.0-ALPHA.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 4. Create a Office 365 E3 trial account and derive the API Key.
	i) 		Using the URL "http://www.yammer.com" create a Yammer trial account.(This user should not be the person who owned the company)
	ii)		Create an app and get an api token by following the instruction given on this URL "https://developer.yammer.com/introduction/#gs-registerapp"
	iii)	A message should be sent to a group by another user notifying the user created in step 4 - i).

 5. Update the Yammer properties file at location "{ESB_Connector_Home}/yammer/yammer-connector/yammer-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 						- 	Use "https://www.yammer.com".
	ii) 	apiToken					-   Use the api token generated in step 4 - ii).
	iii)	postNewMessageBody			- 	Text message body for creating a message.
	iv)		limit						- 	The number of results per page (Should be an integer value greter 											than or equal to 1).
	v)		page						- 	The page number to request in resopnses (Should be an integer value 										greater than or equal to 1).
	vi)		userEmail					-   Email address of an another yammer user in the network rather than 											the current user.
	vii)	topicId			   			-   Id of an existing topic which is subscribed by the user.
	viii)	threadId        			-   Id of an existing thread which is subscribed by the user.
	ix)	    relationshipEmail    		-   Email address of an another yammer user in the network rather than 											the current user.
	x)	    relationshipEmailOpt	    - 	Email address of an another yammer user in the network rather than 											the current user.
	xi)	    relationshipUserId			- 	The user ID of the current user.
	xii)	groupId						- 	Use a valid group ID.
	xiii)	search				    	- 	Use a valid search query (Ex: name of a user).
	xiv)	prefix						- 	Use the first letter of an existing user's first name (Ex: a).
	xv)	    subscribedUserId			- 	Use a user ID of a user who is being followed(subscribed) by the current user.
	xvi)	attachmentFileName			- 	Use the filename which is used as the attachment.
	xvii)	timeOut						- 	Time out value for waiting since the yammer API limit the 													continuous endpoint calls (recommended value is 5000).
		
	Note: userEmail, relationshipEmail and relationshipEmailOpt properties should contain email addresses of 3 different yammer users.
		  relationshipEmail and relationshipEmailOpt should not subscribed to the current user and the current user should not subscribed to above users. 
		  After each run the user mentioned in xv) must subscribed again.
		  After each run remove the relationships of users which belongs the following email addresses 
		  relationshipEmail and relationshipEmailOpt 

 6.  Make sure that the Yammer connector is set as a module in esb-connectors parent pom.
        <module>yammer/yammer-connector/yammer-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install
	  