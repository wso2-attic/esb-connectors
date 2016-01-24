Product: WSO2 ESB Connector for Blogger + Integration Tests
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above

    Tested Platforms: 

    - Ubuntu 13.04, Mac OSx 10.9
    - WSO2 ESB 4.9.0-ALPHA
    - Java 1.7

1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/ and copy the wso2esb-4.9.0-ALPHA.zip in to location "{ESB_Connector_Home}/repository/"..

2. Create trial blogger account and a trial blog and a trial post for test purpose.
      Login and make few Random Comments in the trial post.
      Follow https://developers.google.com/blogger/docs/3.0/using#APIKey to get a api key.
      Use Oauth Playground (https://developers.google.com/oauthplayground/) to create an Oauth 2.0 access token for permission.

3. Navigate to {Blogger_Connector_Home}/src/test/resources/artifacts/ESB/connector/config"  and modify the following properties in blogger.properties file
 
   apiKey= obtained from google api console
   accessToken= obtained from Oauth 2.0 playground
   blogID= blog id of the trial blog
   postID= post id of the trial post
   blogURL= URL of trial blog
   search_query= a search query for search a post with 
   post_path= trial posts path

 4.  Make sure that blogger is specified as a module in ESB_Connector_Parent pom.
        <module>blogger/blogger-connector/blogger-connector-1.0.0/org.wso2.carbon.connector</module>

 5. Navigate to "{ESB_Connector_Home}/" and run the following command.
       "$ mvn clean install"