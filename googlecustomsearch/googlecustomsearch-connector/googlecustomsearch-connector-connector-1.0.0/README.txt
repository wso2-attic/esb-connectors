Product: Integration tests for WSO2 ESB Google Custom Search Connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Linux 3.11.0-19-generic (Ubuntu 14.04LTS), Mac OSx 10.9
 - WSO2 ESB 4.9.0-ALPHA
 - Java 1.7

STEPS:

1. Download ESB 4.9.0-ALPHA by navigating the following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/ and copy that wso2esb-4.9.0-ALPHA.zip file in to location "{ESB_Connector_Home}/repository/".

2. Make sure that googlecustomsearch is specified as a module in ESB_Connector_Parent pom.
       <module>googlecustomsearch/googlecustomsearch-connector/googlecustomsearch-connector-connector-1.0.0</module>

3. Creating a Google Cloud Console account:
	- Go to https://console.developers.google.com/
	- Create a new Google Cloud Console project
	- Go to your newly created project and go to APIs and Auth
	- Enable the Custom Search API
	- Go to Credentials and find Public API access
	- Generate a new API key

4. Creating a Custom Search Engine
	- Go to https://www.google.com/cse/all
	- Create a new search engine and go to control panel and find Search engine ID

5. Copy the connector properties file at "googlecustomsearch/src/test/resources/artifacts/ESB/connector/config/googlecustomsearch.properties".
    i)  apiKey - Use the API key you got from step 3.
    ii) cseID - Use the Search engine ID you got from step 4.

6. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install

NOTE => The Custom Search API allows only 100 queries per day for free,
	if you want to search more than 100, you have to paid for it 
	or create a new API key and use it.
	
    apiKey:AIzaSyBAj-H1k2IGT19ZTqp_UwZzJmzvzQvV4tw
	cscID:014869045608377880101:18pywcgrwls
	
	You can use instead of cscID. For testing use cref=http://www.guha.com/cref_cse.xml
	or manually create a custom search engine specification and host 
	it into your website and use it.