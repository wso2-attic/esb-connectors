Product: WSO2 ESB Connector for Facebook + Integration Tests
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1
    
1. To build the connector without running tests from any location, run maven build with the -Dmaven.test.skip=true switch.

2. Before attempting to run integration tests, uncomment the following in pom.xml:

    i)  <parent>
            <groupId>org.wso2.esb</groupId>
            <artifactId>esb-integration-tests</artifactId>
            <version>4.8.1</version>
            <relativePath>../pom.xml</relativePath>
        </parent>

    ii) <dependency>
            <groupId>org.wso2.esb</groupId>
            <artifactId>org.wso2.connector.integration.test.base</artifactId>
            <version>4.8.1</version>
            <scope>system</scope>
            <systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>
        </dependency> 
Note:

	This test suite can execute based on two scenarios.
		1. Use the given test account and parameters. - in this scenario you only need to replace accessToken , pageAccessToken in property file
		2. Setup new facebook account and follow all the instruction given below
	
Steps to follow in setting integration test.



 3.  Download ESB 4.8.1 from official website.

 4.  Deploy following patches.
            patchjson
            Empty-payload-patch
            special-char-on-get
            multipart-patch
            http PATCH request patch

 5.  Navigate to location "/wso2esb-4.8.1/repository/conf/axis2" and add/uncomment following lines in "axis2.xml".
    
            <messageFormatter contentType="text/javascript" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

            <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

            <messageBuilder contentType="text/javascript" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

            <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

            <messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>

            <messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>

            <messageFormatter contentType="application/octet-stream" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

            <messageBuilder contentType="application/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 6.  Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "Integration_Test/products/esb/4.8.1/modules/distribution/target/".

 7.  Make sure "integration-base" project is placed at "Integration_Test/products/esb/4.8.1/modules/integration/"

 8.  Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      $ mvn clean install
      
 9.  Add following dependency to the file "Integration_Test/products/esb/4.8.1/modules/integration/connectors/pom.xml"
     <dependency>

         <groupId>org.wso2.esb</groupId>

         <artifactId>org.wso2.connector.integration.test.base</artifactId>

         <version>4.8.1</version>

         <scope>system</scope>

         <systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>

     </dependency>
  
 10.  Copy Facebook connector, "facebook.zip" to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository/"

 11.  Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"

        <test name="Facebook-Connector-Test" preserve-order="true" verbose="2">
            <packages>
                <package name="org.wso2.carbon.connector.integration.test.facebook"/>
            </packages>
        </test> 
         
 12. Copy the java file "facebook/integration-test/src/test/java/org/wso2/carbon/connector/integration/test/facebook/FacebookConnectorIntegrationTest.java" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/java/org/wso2/carbon/connector/integration/test/facebook/"
 
 13. Copy proxy files from location "facebook/integration-test/src/test/resources/artifacts/ESB/config/proxies/facebook/" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/proxies/facebook/"

 14. Copy request files from location "facebook/integration-test/src/test/resources/artifacts/ESB/config/restRequests/facebook" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/restRequests/facebook/" 
    
 15. Copy resource files from location "facebook/integration-test/src/test/resources/artifacts/ESB/config/resources/facebook" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/resources/facebook/" 

 16. Copy Property File, "facebook.properties" from location "facebook/integration-test/src/test/resources/artifacts/ESB/connector/config" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config/" 
     and edit using valid and relevant data.
        
 17. Prerequisites for Facebook Connector Integration Testing

    Follow these steps before start testing.
    a)  Create a fresh account in Facebook and Log on to https://www.facebook.com/ with the web browser.
    b)  Obtain "id" using me/?fields=id in "Graph Explorer" (https://developers.facebook.com/tools/explorer) and copy in to userId in property file.
    c)  Add some friends to the profile by sending friend requests or accepting friend requests in Facebook.
    d)  Make sure the user is one of the Administrator(s) of a page which has more than 50 likes and copy in to pageId in property file.
    e)  Your account should be a verified developer account. and create a new app with canvas URL using your verified account.
    f)  Obtain access_token of the application (select appropriate application name in "Application:" DropBox and select all permissions in "Select Permissions" Popup window.) with "Get Access Token" in "Graph Explorer"; and put it as accessToken in Property File.
    g)  In "Graph Explorer" send a get request as /me/accounts 
        *    This will output all the pages you have created. Select page which has at least 50 likes and copy "id" in to pageId and "access_token" in to pageAccessToken in Property File.
    h)  Navigate to App Dashboard and copy "App ID" in to clientId and "App Secret" in to clientSecret in Property File.
    i)  Create a new group and obtain "id" and copy in to userGroupId in Property File.
    j)  Create an achievement URL and copy in to achievementURL in property file; and create callback URL and copy in to achievementURL in property file by refering the following links.
        * https://developers.facebook.com/docs/games/achievements#achievementtags 
        * https://developers.facebook.com/docs/graph-api/real-time-updates/#setup
    k)  Add one of the friends as an Administator or Developer to Application with "Roles" tab of the "Roles" section of "Dashboard" of the application and copy user "id" in to appUserId in property file.
    l)  Create test users with "Test Users" tab of the "Roles" section of "Dashboard" and copy one of the User ID in to groupMember in property file.
    m)  Create a conversation under page and using {pageId}/conversations in "Graph Explorer" copy "id" of a conversation in to conversationId in property file.
    n)  Create messages with friends and using me/threads in "Graph Explorer" copy "id" of a message thread in to threadId in property file.
    
    * The appUser must be added to the Application before each test run.
    * accessToken and pageAccessToken needs to be updated before each test run.
        
    o)  Following fields in the property file also should be updated appropriately.

        1)    friendId is Id of a friend who can be tagged to a photo and an invitation can be sent to.

        2)    userId is the profile ID of the user.

        3)    timeOut is the default time out to be used.

        4)    sourceUserId is the primary user and targetUserId is the secondary user to be checked whether friends or not.

        5)    userAId is the primary user and userBId is the secondary user who must have some mutual friends.

        6)    href is the relative path for the notification URL to be published.

        7)    template is the message text of the notification to be published.

        8)    achievementURL is the URL of an Open Graph Achievement object.

        9)    callbackURL is the call back URL of app subscription, that will receive request when an update is triggered.

        10)   subscriptionObject indicates the object types {user, page, permissions, payments}. for payments Ad account is required.

        11)   subscriptionFields is one or more of the set of valid fields in this object to subscribe to, in method CreateAppSubscription.

        12)   verifyToken is the verify_token of the application subscription.

        13)   displayOrder is the order of the achievement when shown in any achievement stories UI.

        14)   pageId is the page Id which received 50 likes.

        15)   groupName is a suitable name for a group to be created.

        16)   description is a general Description to be used.

        17)   albumName ia a suitable name for an album to be created.

        18)   message is a general Message to be used.

        19)   eventName is a suitable name for an event to be created.

        20)   about is a suitable clause to be used as about in page details.

        21)   contactEmail is a valid email address with which users can contact developers.

        22)   appUserId is a third party user who is added to the Application, and will be banned and unbanned.

        23)   pageUsreId is a third party user who is a page user and used to be blocked.

        24)   appId is the application Id.

        25)   canvasUrl is the canvas URL of the application.

        26)   statusMessage is a suitable message for status.

        27)   statusLink is a link to be used in user status.

        28)   statusLinkMessage is a message suitable to be used as link Message in status

        29)   offerTitle is any text less than 90 characters for an offer title. 

        30)   offerExpirationTime is a future date to be given in page offer as expiration time

        31)   eventStartTime is a future date to be given in an event as starting time.

        32)   eventWallPost is a message suitable for a posting.

        33)   commentMessage is a message suitable for a commenting.

        34)   noteMessage is the body of the note to be created.

        35)   noteSubject is the subject of the note to be created.

        36)   multipartProxyName is the name of the proxy to be used in methods with uploading multipart form data

        37)   userGroupId is the Id of the user group which requires details.

        38)   threadId is the Id of the message thread which requires details.

        39)   messageId is the Id of the message which requires details.

        40)   imageName is the name of the image file to be uploaded.

        41)   groupMember is the Id of the user to be invited to a user group and must be a test user of the application.

        42)   value is to be used in update page settings (must be a boolean value).

        43)   conversationId is a thread id of a group conversation.

        44)   role is an application user role can be ['administrators', 'developers', 'testers', 'insights users']

        45)   coverUrl is any photo URL, compatible with cover image.

        46)   videoName is the video name file to be uploaded; The dimensions of the video must be as of the Graph API document.

        47)   pageVideoName is the video name file to be uploaded to the page.

        48)   videoUploadTimeOut is time duration between Video upload call and information retrieval call.

        49)   apiUrl is the URL of the facebook Graph API.

        50)   friendListName is a suitable name for friend List to be created.
        
18. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
     $ mvn clean install

	 
	 credential of test account:
	 login:	virtusa.wso2.connector@gmail.com
	 password: 2wsx3edc#