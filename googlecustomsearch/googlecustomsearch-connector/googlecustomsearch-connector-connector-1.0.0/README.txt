Product: Integration tests for WSO2 ESB Google Custom Search Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform:

 - Linux 3.11.0-19-generic (Ubuntu 14.04LTS)
 - WSO2 ESB 4.8.1

STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/googlecustomsearch-connector/googlecustomsearch-connector-1.0.0/org.wso2.carbon.connector/repository/"

2. Make sure "integration-base" project is placed at "{basedir}/../"

3. Navigate to "integration-base" and run the following command.
      $ mvn clean install

4. Make sure the Google Custom Search test suite is enabled (as given below) and all other test suites are commented in the following file "{basedir}/src/test/resources/testng.xml"
    
      <test name="GoogleCustomSearch-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.googlecustomsearch"/>
        </packages>
    </test>

5. Creating a Google Cloud Console account: 
	- Go to https://console.developers.google.com/
	- Create a new Google Cloud Console project
	- Go to your newly created project and go to APIs and Auth
	- Enable the Custom Search API
	- Go to Credentials and find Public API access
	- Generate a new API key

6. Creating a Custom Search Engine
	- Go to https://www.google.com/cse/all
	- Create a new search engine and go to control panel and find Search engine ID

7. Copy the connector properties file at "googlecustomsearch/src/test/resources/artifacts/ESB/connector/config/googlecustomsearch.properties".
    i)  apiKey - Use the API key you got from step 8.
    ii) cseID - Use the Search engine ID you got from step 9.

8. Navigate to "${basedir}/" and run the following command.
      $ mvn clean install

NOTE => The Custom Search API allows only 100 queries per day for free,
	if you want to search more than 100, you have to paid for it 
	or create a new API key and use it.
	
        apiKey:AIzaSyBAj-H1k2IGT19ZTqp_UwZzJmzvzQvV4tw
	cscID:014869045608377880101:18pywcgrwls
	
	You can use instead of cscID. For testing use cref=http://www.guha.com/cref_cse.xml
	or manually create a custom search engine specification and host 
	it into your website and use it
