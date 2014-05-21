Product: WSO2 ESB Connector for Blogger + Integration Tests
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above

    Tested Platforms: 

    - Ubuntu 13.04
    - WSO2 ESB 4.8.1
    


1. Copy the ESB with latest patches as wso2esb-4.8.1.zip to location "Integration_Test/products/esb/4.8.1/modules/distribution/target/".

2. Copy the contents of blogger home folder to the location "Integration_Test/products/esb/4.8.1/modules/integration/blogger" 

3. To build the connector run maven build with the -Dmaven.test.skip=true switch from location "Integration_Test/products/esb/4.8.1/modules/integration/blogger" .

4. Copy the blogger.zip file to the location "Integration_Test/products/esb/4.8.1/modules/integration/blogger/repository" 
 
5. Create trial blogger account and a trial blog and a trial post for test purpose. Go to google api console and get a api key. Use Oauth Playground to create an Oauth 2.0 access token for permission. 
 
6. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/blogger/src/test/resources/artifacts/ESB/connector/config"  and modify the following properties in blogger.properties file 
 
   apiKey= obtained from google api console
   accessToken= obtained from Oauth 2.0 playground
   userID= user id of the trial blog
   blogID= blog id of the trial blog
   postID= post id of the trial post
   blogURL= URL of trial blog
   search_query= a search query for search a post with 
   post_path= trial posts path 

 7. Before attempting to run integration tests, uncomment the following in pom.xml:

       <parent>
            <groupId>org.wso2.esb</groupId>
            <artifactId>esb-integration-tests</artifactId>
            <version>4.8.1</version>
            <relativePath>../pom.xml</relativePath>
        </parent>

    Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/blogger/" folder and run command     " $ mvn clean install "
 
 
 