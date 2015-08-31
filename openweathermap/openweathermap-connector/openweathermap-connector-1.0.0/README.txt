Product: WSO2 ESB Connector for openweathermap + Integration Tests

 Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
                 https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

    Tested Platforms:

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.9.0-BETA



Steps to follow in setting integration test.


 1. Download ESB 4.9.0-BETA-SNAPSHOT by navigating to the following URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/
 


 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.

 3. ESB should be configured as below.
    Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml)

        Message Formatters :-
	<messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
	<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

        Message Builders :-
	<messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
	<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>



 4. Compress modified ESB as wso2esb-4.9.0-BETA.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME >/repository/".


 5. Create a openWeatherMAp  account and derive the API Key.
       i)  Using the URL "http://openweathermap.org" create a openWeatherMap.


 6. Make sure that openweathermap is specified as a module in ESB Connector Parent pom.
            <module>openweathermap/openweathermap-connector/openweathermap-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Update the openweathermap properties file at location "<OPENWEATHERMAP_CONNECTOR_HOME>/openweathermap-connector/openweathermap-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

        i)      apiUrl 				- 	The API URL specific to the created openWeatherMap account (http://openweathermap.org).
    	ii) 	apiKey			        -       Use the api key obtained under step 5.
    	iii)	apiVersion		        -	Use a appropriate API Version.
    	iv)     cityID			        -	Use a valid integer value for the city ID.
    	v)	cityName		        - 	Use a valid Country name.
    	vi)	zipCode				-       Use a valid numeric value for zipCode.
    	vii)	countryCode			-	Use a valid Country Code (E.g. uk, us).
    	viii)   lat			        -	Use a valid numerical value for City geo location, longitude.
    	ix)	lon			        -	Use a valid numerical value for City geo location, latitude.
    	x)	count              	        -	Use a integer value for count.
    	xi) 	cluster  		        -	use server clustering of points. Possible values ​​are [yes, no].
    	xii)	cnt                 		-	Use a integer value for cnt.
    	xiii)	lang			        -	Use a valid string language (E.g. en, ru).
    	xiv)	bbox			        - 	Use a set of numerical values for bounding box [lat of the top left point, lon of the top left point, lat of the bottom right point, lon of the bottom right point, map zoom].
    	xv) 	cityIDs			        -	Use a valid integer values for the city IDs(E.g. 524901,703448,2643743 ).
    	xvi)	units			        -	Use a valid string such as  Standard, metric, and imperial units are available.
    	xvii)	type			        -	Use accuracy level either use the 'accurate' or 'like' as type .
    	xviii)	mode			        -	possible values are xml and html. If mode parameter is empty the format is JSON by default.
    	xix)	types			        -	use a types as 'hour'.
    	xx)	start			        -	Use a valid start date with the format of (unix time, UTC time zone)(E.g. start=1369728000 ).
    	xxi)	end			        -	Use a valid end date with the format of (unix time, UTC time zone)(E.g. start=1369728000 ).
    	xxii)	stationID			-	Use a valid station ID(E.g. 29584).
    	xxiii)  frequencyType			-       Frequency of data, possible parameters are 'daily', 'hour', and 'tick'. Parameter 'tick.
    	xxiv) 	callback		        -	Use JavaScript code you can transfer callback functionName to JSONP callback


 8.  Navigate to "{ESB_Connector_Home}/" and run the following command.
             $ mvn clean install
