Product: Integration tests for WSO2 Tumblr connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - UBUNTU 13.10
 - WSO2 ESB 4.9.0-ALPHA

STEPS:
	  
1. 	Make sure the ESB 4.9.0-ALPHA zip file available at "{ESB_CONNECTORS_HOME}/repository/"
	  
2.	Create Tumblr account and apply for application and derive consumer/api key
		2.1 Using URL https://www.tumblr.com/register create tumblr account.
		2.2 Follow URL https://www.tumblr.com/oauth/apps and register your application and retrieve 
			OAuth consumer key and consumer secret. 
		2.3 Follow steps mentioned in "About Tumblr OAuth" in  https://www.tumblr.com/oauth/apps
			and retrieve access token and access token secret.
			or 
			nevigate to https://api.tumblr.com/console/calls/user/info and press show keys to retrieve 
			access token and access token secret [NOTE : PLEASE USE ACCESS TOKENS DERIVED FROM THIS 
			APPROACH ONLY FOR TESTING]

3.	Update the Tumblr properties file at location "{ESB_CONNECTORS_HOME}/tumblr/tumblr-connector/tumblr-connector-1.0.0/org.wso2.carbon.connector
	/src/test/resources/artifacts/ESB/connector/config"
	
	i	blogHostUrl - Name of the blog created in step 2.1
	ii	consumerKey - Consumer key retrieved in step 2.2
	iii	consumerSecret - Consumer secret retrieved in step 2.2
	iv	accessToken - Access token retrieved in step 2.3
	v	tokenSecret - Access token secret retrieved in step 2.3
	vi	reblogKey - reblog key of your favourite blog post
	
			Retrieving reblog key - 
				1. retrieve from url of the reblog icon of the post
             eg : https://www.tumblr.com/reblog/91171443335/Wuty3iOW?redirect_to=%2Fdashboard%2F2%2F91230209122
                                                           ===========
					 		reblog key is - Wuty3iOW
	
  				2. use api console https://api.tumblr.com/console/calls/blog/posts
  				
  				
  	vii reblogPostId - post id of the post you selected above  
  	
  			Retrieving reblog key - 
				1. retrieve from url of the reblog icon of the post
            eg : https://www.tumblr.com/reblog/91171443335/Wuty3iOW?redirect_to=%2Fdashboard%2F2%2F91230209122
                                               ===========
					 		post id is - 91171443335
	
  				2. use api console https://api.tumblr.com/console/calls/blog/posts
  				
  	viii directLikeReblogKey - reblog key of a post	to test like operation
  	ix   directLikePostId - post id of the post selected above in (viii)
  	x    esbLikeReblogKey - reblog key of another post	to test like operation
  	xi   esbLikePostId - post id of the post selected above in (x)
  	xii  followBlogUrl - url of your favourite blog
  	xiii postTag - a post tag to search

  	Note: Download and add the scribe-1.3.6.jar into {ESB_CONNECTORS_HOME}/tumblr-connector/tumblr-connector-1.0.0/org.wso2.carbon.connector/src/main/resources/lib/

4.  Make sure that the tumblr connector is set as a module in esb-connectors parent pom.
        <module>tumblr/tumblr-connector/tumblr-connector-1.0.0/org.wso2.carbon.connector</module>

5.	Navigate to "{ESB_CONNECTORS_HOME}/" and run the following command.
      				$ mvn install
  					
NOTE: Following Tumblr account can be used for integration test
	Email: tumblresbconnector@gmail.com
	Password: wso2esbconnector
	OAuth Consumer Key / Api key:34Fu9sc1XV012BEolDuTZcD5ykY6Zz18nJ1yWgD4qpW71IZs5t
	Consumer Secret Key:  0dgqsbhlXnVXdrnCFkBvscTAEEnxmqIsJbYUJaFPIbwuYOTb2M
	Access Token: DTGDykJO9PkKVeeCFt4LfNOuegcnQjwct7QZd63ejKFzbHgrsx
	Access token secret: KZeu3jshegCrbgZ6sgOCQDnLz8c7M2DiAPUvJ4h7AKvCoeXW1V

