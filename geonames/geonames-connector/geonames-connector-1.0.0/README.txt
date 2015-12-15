Product: Integration tests for WSO2 ESB GeoNames connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0
 - Java 1.7
 
Steps to follow in setting integration test.

 1. Download ESB 4.9.0 by navigating to the following URL: 
    http://wso2.com/products/enterprise-service-bus/# 
 
 2. Deploy relevant patches, if applicable. Place the patch files into location "<ESB_HOME>/repository/components/patches".
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
      <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
      <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Set authorization details:
   i)    Using the URL "http://www.geonames.org/login" create a GeoNames account.
   ii)   On the Credential page, create the username , password for further use.(parameter 'username' needs to be passed with each request)
   iii)  Once the credentials are given, make sure to activate the account by confirming the email receieved.
   iv)   Enable the web services by navigating to "http://www.geonames.org/login" .
   
 5. Compress ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".

 6. Make sure that geonames is specified as a module in ESB_Connector_Parent pom.
    <module>geonames/geonames-connector/geonames-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Update the Geonames properties file at location "<GEONAMES_CONNECTOR_HOME>/geonames-connector/geonames-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
      
   i)    apiUrl                     -  The API URL of Geonames(e.g. http://www.geonames.org/).
   ii)   username                   -  Use the username obtained under step 4 ii).
   iii)  query                      -  Text to be used as 'query' which is an optional parameter in search method.(e.g : Noank).
   iv)   name                       -  Text to be used as 'name' which is an optional parameter in search method. (e.g : Noank).
   v)    nameStartsWith             -  Text to be used as 'nameStartsWith' which is an optional parameter in search method. (e.g : N).
   vi)   adminCode1                 -  Text to be used as 'adminCode1' which is an optional parameter in search method. (Make sure to use an existing admin code. e.g : CT).
   vii)  contryCode                 -  Text to be used as 'contryCode' which is an optional parameter in search method. (Make sure to use an existing country code. e.g : US).
   viii) maxRows                    -  Integer value to be used as 'maxRows' while listing WeatherStations with optional parameters.(Make sure to use  '1' or '2').
   ix)   radius                     -  Decimal value to be used as 'radius' while listing WeatherStations with optional parameters.(e.g : 0.8).
   x)    north                      -  Decimal value to be used as the coordinate of the 'North' in order to get valid bounding box.(Make sure to use '44.1' as the coordinate to get a valid response).
   xi)   south                      -  Decimal value to be used as the coordinate of the 'South' in order to get valid bounding box.(Make sure to use '9.9' as the coordinate to get a valid response).
   xii)  east                       -  Decimal value to be used as the coordinate of the 'east' in order to get valid bounding box.(Make sure to use '22.4' as the coordinate to get a valid response).
   xiii) west                       -  Decimal value to be used as the coordinate of the 'west' in order to get valid bounding box.(Make sure to use '55.2' as the coordinate to get a valid response).
   xiv)  language                   -  Text to be used as 'language' while listing citiesAndPlacenames which represents the language of a country. (E.g : en ).
   xv)   postalCode                 -  Text to be used as 'postalCode' which is an optional parameter in postalCodeSearch method. (Make sure to user existing postal code. E.g :9011).
   xvi)  postalCodeStartsWith       -  Text to be used as 'postalCodeStartsWith' which is a optional parameter in postalCodeSearch method. (Make sure to use first two integer values of an existing postal code. E.g : 90)
   xvii) longitude                  -  Decimal value to be used as 'longitude' which determines the position of a particular place. (e.g : -73.96625).
   xviii)latitude                   -  Decimal value to be used as 'latitude' which determines the position of a particular place. (e.g : 40.78343).
   xix)  date                       -  Date of the getTimezone,listRecentEarthquakes methods with optional parameters. A date in the past format as 'yyyy-MM-dd'.(Make sure not to use a future date.)
 
   Note : i)   The values given in xvii) and xviii) should reflect an exact position of a some place on the earth.
          ii)  Make sure that the adminCode1(vi) value is from a place of the country which is providing as the countryCode(vii).
          iii) The value given to the name(iv), nameStartsWith(v), query(iii) should reflect to the place which the adminCode1(v) associated with.
          iv)  The postalCode(xv) and  postalCodeStartsWith(xvi) should be the existing postal codes in the country which is given as the countryCode(vii).
 
 8. Navigate to "{ESB_CONNECTOR_HOME}/" and run the following command.
      $ mvn clean install
