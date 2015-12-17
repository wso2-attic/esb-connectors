Product: WSO2 ESB Connector for googlespreadsheet + Integration Tests

 Pre-requisites:

    - Maven 3.x
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
                 https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

    Tested Platforms:

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.9.0
    - java 1.7



Steps to follow in setting integration test.


 1. Download ESB 4.9.0  by navigating to the following URL: http://wso2.com/products/enterprise-service-bus/#


 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.

 3. ESB should be configured as below.
    Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml)

	    Message Formatters :-
	    <messageFormatter contentType="application/atom+xml"
                                  class="org.apache.axis2.transport.http.ApplicationXMLFormatter"/>
        <messageformatter contenttype="text/csv"
        						   class="org.apache.axis2.format.PlainTextFormatter"/>

		Message Builders :-
		<messageBuilder contentType="application/atom+xml"
                                class="org.apache.axis2.builder.ApplicationXMLBuilder"/>
        <messagebuilder contenttype="text/csv"
        			 			class="org.apache.axis2.format.PlainTextBuilder"/>


 4. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "<ESB_CONNECTORS_HOME>/repository/".


 5. Get a access token from OAuth 2.0 Playground.
       i)  Using the URL "https://developers.google.com/oauthplayground/" create a access token and refresh token.

      Note: Application needs access to user data, it asks Google for a particular scope of access.
            Here's the OAuth 2.0 scope information for the Google Sheets API:"https://spreadsheets.google.com/feeds"


 6. Make sure that googlespreadsheet is specified as a module in ESB Connector Parent pom.
		<module>googlespreadsheet/googlespreadsheet-connector/googlespreadsheet-connector-1.0.0/org.wso2.carbon.connector</module>

 7. Update the googlespreadsheet properties file at location "<GOOGLESPREADSHEET_CONNECTOR_HOME>/googlespreadsheet-connector/googlespreadsheet-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

        i)		refreshToken 					- 	Use the Refresh token.
    	ii) 	clientId						-   Use the Client ID.
    	iii)	clientSecret					-	Use the Client Secret.
    	iv)     accessToken				        -	Use the accessToken in step 5.
    	v)		apiVersion				        - 	Use appropriate API version.
    	vi)		apiUrl						    -   Use the API URL of the google spreadsheet.
    	vii)	key						        -	Use a Unique value of the spreadsheet.
    	viii)   exportFormat				    -	Use a formate of extracting spreadsheet data(eg.csv,tsv).
    	ix)	    spreadsheetTitle				-	Use a title of the spreadsheet.
    	x)		rowCount              			-	Use a required number of row to a new worksheet.
    	xi) 	colCount  			            -	use a required number of column to a new worksheet.
    	xii)	worksheetId                 	-	Use the ID of the worksheet.
    	xiii)	worksheetVersion			    -	Use the version of the worksheet.
    	xiv)	worksheetTitle				    - 	Use the title of worksheet.
    	xv) 	rowId					        -	Use the ID of the row.
    	xvi)	fieldValues					    -	Use a set of key/value pairs that you can insert to a row.
    	xvii)	inputURL					    -	Use URL which has csv or tsv format datas.
    	xviii)	row						        -	Use a particular row index.
    	xix)	col					            -	Use a particular column index.
    	xx)		cellVersion				        -	Use a valid version of the cell.
    	xxi)	inputValue			            -	Use a value or string that is to be updated in the cell.
    	xxii)	cellId						    -	Use a particular ID of the cell.
    	xxiii)  inputTableURL					-   Use URL which has tables or lists in the particular web page.
    	xxiv) 	minRow				            -	Use a starting row index.
    	xxv)    maxRow                          -	Use a ending row index.
        xxvi)   minCol                          -	Use a starting column index.
        xxvii)  maxCol                          -	Use a ending column index.
        xxviii) rowVersion                      -	Use the version of the row.
        xxviX)  title                          -	Use a new title for new worksheet.


        Note:- The property values of title,cellVersion,rowVersion and worksheetVersion should be changed  for each integration execution.


 8.  Navigate to "{ESB_Connector_Home}/" and run the following command.
             $ mvn clean install
