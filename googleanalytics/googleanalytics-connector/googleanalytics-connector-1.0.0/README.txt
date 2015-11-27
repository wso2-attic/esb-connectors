Product: Integration tests for WSO2 ESB GoogleAnalytics connector

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

 1. Download ESB 4.9.0 by navigating to the following URL: http://wso2.com/products/enterprise-service-bus/#
 
 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
     If required add the X.509 certificate from http://www.google.com/analytics/ to the client-truststore.jks of the ESB located in <ESB_HOME>/repository/resources/security folder
      and wso2carbon.jks located in <GOOGLEANALYTICS_CONNECTOR_HOME>/googleanalytics-connector/googleanalytics-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products.

 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
      <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
      <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 4. Set authorization details:
   i)   Using the URL "https://analytics.google.com/analytics/web/provision?et=&authuser=#provision/SignUp/" create a Google Analytics account.
   ii)  Go to "https://developers.google.com/oauthplayground/".
   iii) Authorize GoogleAnalytics API from "Select & authorize APIs" by selecting all the scopes available.
   iv)  Then go to "Exchange authorization code for tokens" and click on "Exchange authorization code for token" button and get the access token from "Access token" box (Note down the access token for future use.).
   v)   Go to "https://console.developers.google.com/" and log in with the created google account and create a new project using the drop down in the top bar(Note down the project Id for future use.). 
   vi)  Enable GoogleAnalytics API by navigating to the "APIs" tab which is under "APIs & auth" tab.
   vii) Go to "Credentials" tab which is under "APIs & auth" tab and add credentials by selecting OAuth 2.0 client ID option.( Configure consent screen and then create client ID for 'Web application' type of applications. Note down the redirect uri for future use.)
   viii)Note down the client ID and client secret for future use.
   ix)  Get the authorization code by sending a GET request using url, https://accounts.google.com/o/oauth2/auth?redirect_uri=<redirect_uri>&response_type=code&client_id=<client_ID>&scope=https://www.googleapis.com/auth/analytics https://www.googleapis.com/auth/analytics.readonly https://www.googleapis.com/auth/analytics.provision https://www.googleapis.com/auth/analytics.manage.users https://www.googleapis.com/auth/analytics.edit https://www.googleapis.com/auth/analytics.manage.users.readonly&approval_prompt=force&access_type=offline (Replace <redirect_uri> and <client_ID> with the redirect uri and client ID values noted in step vii and viii. Note down the authorization code for future use.)
   x)   Get the access token and refresh token by sending a POST request to the url https://www.googleapis.com/oauth2/v3/token with x-www-form-urlencoded body with code,client_id,client_secret,redirect_uri values noted before and with grant_type value "authorization_code" (Note down the access token and refresh token for future use.).
   xi)  Add following resources to the ESB registry with the noted values before.

      /_system/governance/connectors/GoogleAnalytics/accessToken
      /_system/governance/connectors/GoogleAnalytics/apiUrl  
      /_system/governance/connectors/GoogleAnalytics/clientId
      /_system/governance/connectors/GoogleAnalytics/clientSecret
      /_system/governance/connectors/GoogleAnalytics/redirectUrl
      /_system/governance/connectors/GoogleAnalytics/refreshToken

 
 5. Compress modified ESB as wso2esb-4.9.0.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 6. Pre-requisites to follow.
   i)    Navigate to the 'Admin' section of the account created in 4 i) 
            a) Go to the 'Account Settings' tab of the selected account and obtain the account ID.
            b) Navigate to the 'PROPERTY' section ->Tracking Info->Tracking Code and obtain the Tracking ID.
            c) Navigate to the 'VIEW' section ->View Settings and obtain the view ID.
   ii)   Navigate to the 'Admin' section->'PROPERTY'->AdWords Linking and create a new AdWords Account. Obtain the customer ID after the account creation. 
 
 
 7. Update the GoogleAnalytics properties file at location "<GOOGLEANALYTICS_CONNECTOR_HOME>/googleanalytics-connector/googleanalytics-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

   i)      apiUrl                             -   The API URL of GoogleAnalytics(e.g. https://www.googleapis.com) .
   
   ii)     accountId                          -   Use the Account ID obtained under Step 6 i) a). 
   iii)    webPropertyId                      -   Use the tracking ID obtained under Step 6 i) b).
   iv)     fields                             -   Fields which need to be there in the response as a comma separated string. Make sure to include 'id' and 'kind' and not to include 'selfLink' in the string.
   v)      listFields                         -   Fields which need to be there in the response of the list methods as a comma separated string. Make sure to include 'kind','username','startIndex','items','itemsPerPage' and not to include 'selfLink' in the string..
   vi)     prettyPrint                        -   Use 'true' or 'false' as the value for prettyPrint. (Make sure to use a boolean value).
   
   vii)    coreReportIds                      -   Use the view ID obtained under Step 6 i) c) and append it with 'ga'(E.g ga:123434) .  
   viii)   coreReportStartDate                -   Text to be used as 'start-date' to fetch analytics data. A past date in the format 'yyyy-MM-dd' should be used.
   ix)     coreReportEndDate                  -   Text to be used as 'end-date' to fetch analytics data. A date in the format 'yyyy-MM-dd' should be used.
   x)      coreReportMetrics                  -   Comma separated string which contains set of metrics (E.g : ga:sessions,ga:bounces).
   xi)     coreReportMaxResults               -   Integer value to be used as 'max-results' while retrieving report data with optional parameters. (Make sure to use  '1' or '2').
   xii)    coreReportStartIndex               -   Integer value to be used as 'start-index' while retrieving report data with optional parameters. (Make sure to use  '1' or '2').
   
   xiii)   filterName                         -   Text to be used as 'Name' which is a mandatory parameter while creating a Filter.
   xiv)    filterType                         -   Text to be used as 'Type' which is a mandatory parameter while creating a Filter. (Make sure to use the value 'INCLUDE' as the type).
   xv)     filterIncludeExpression            -   Text to be used as 'IncludeExpression' which is a mandatory parameter while creating a Filter.
   xvi)    filterUpdatedName                  -   Text to be used as 'Name' while updating a Filter with mandatory parameters.
   xvii)   filterUpdatedType                  -   Text to be used as 'Type' while updating a Filter with mandatory parameters. (Make sure to use the value 'LOWERCASE' as the type).
   
   xviii)  userLinkEmail                      -   Text to be used as 'email' while creating a user link with mandatory parameters. (E.g conntest@gmail.com).
   xix)    listLinkFields                     -   Fields which need to be there in the listAccountUserLinks response as a comma separated string. Make sure to include 'startIndex','items','totalResults','kind' and not to include 'selfLink' in the string.
   xx)     linksMaxResults                    -   Integer value to be used as 'max-results' while retrieving user links with optional parameters. (Make sure to use '1').
   
   xxi)    dimensionName                      -   Text to be used as 'Name' which is a mandatory parameter while creating a Dimension.         
   xxii)   dimensionScope                     -   Text to be used as 'Scope' which is a mandatory parameter while creating a Dimension.(Make sure to use the one of these values as the scope :'HIT','SESSION','USER' or 'PRODUCT' ).
   xxiii)  dimensionActive                    -   Text to be used as 'Active' which is a mandatory parameter while creating a Dimension.(Make sure to use a boolean value).
   xxiv)   updatedDimensionName               -   Text to be used as 'Name' while updating a Dimension with mandatory parameters.
   xxv)    updatedDimensionScope              -   Text to be used as 'Scope' while updating a Dimension with mandatory parameters.(Make sure to use the one of these values as the scope :'HIT','SESSION','USER' or 'PRODUCT').
   xxvi)   updatedDimensionActive             -   Text to be used as 'Active' while updating a Dimension with mandatory parameters.(Make sure to use a boolean value).
   xxvii)  patchDimensionName                 -   Text to be used as 'Name' while creating a patch with optional parameters. 
   xxviii) patchDimensionScope                -   Text to be used as 'Scope' while creating a patch with optional parameters.(Make sure to use the one of these value as the scope :'HIT','SESSION','USER' or 'PRODUCT').
   xxix)   patchDimensionActive               -   Text to be used as 'Active' while creating a patch with optional parameters. (Make sure to use a boolean value).
   
   xxx)    metricsName                        -   Text to be used as 'Name' which is a mandatory parameter while creating a Metrics.
   xxxi)   metricsScope                       -   Text to be used as 'Scope' which is a mandatory parameter while creating a Metrics.(Make sure to use the value 'HIT' or 'PRODUCT' as the scope).  
   xxxii)  metricsActive                      -   Text to be used as 'Active' which is a mandatory parameter while creating a Metrics. (Make sure to use a boolean value).
   xxxiii) metricsType                        -   Text to be used as 'Type' which is a mandatory parameter while creating a Metrics.(Make sure to use the value 'INTEGER' or 'CURRENCY' as the Type).
   xxxiv)  updatedMetricsName                 -   Text to be used as 'Name' while updating a Metrics with mandatory parameters.
   xxxv)   updatedMetricsScope                -   Text to be used as 'Scope' while updating a Metrics with mandatory parameters.(Make sure to use the value 'HIT' or 'PRODUCT' as the scope).
   xxxvi)  updatedMetricsActive               -   Text to be used as 'Active' while updating a Metrics with mandatory parameters.(Make sure to use a boolean value).
   
   xxxvii) multiChannelIds                    -   Use the view ID obtained under Step 6 i) c) and append it with 'ga'(E.g ga:123434). 
   xxxviii)multiChannelStartDate              -   Text to be used as 'start-date' to fetch data. A past date in the format 'yyyy-MM-dd' should be used.
   xxxix)  multiChannelEndDate                -   Text to be used as 'end-date' to fetch data. A date in the format 'yyyy-MM-dd' should be used.
   xL)     multiChannelMetrics                -   Comma separated string which contains set of metrics (E.g : ga:sessions,ga:bounces).
   xLi)    multiChannelMaxResults             -   Integer value to be used as 'max-results' while retrieving channel data  with optional parameters. (Make sure to use  '1' or '2'). 
   xLii)   multiChannelStartIndex             -   Integer value to be used as 'start-index' while retrieving channel data with optional parameters. (Make sure to use  '1' or '2').
   
   xLiii)  reportType                         -   Text to be used as report-type of the meta data API (E.g : ga).
   xLiv)   configFields                       -   Fields which need to be there in the  listConfigurationData response as a comma separated string. Make sure to include 'kind','etag','items' and 'attributeNames' and not to include 'totalResults' in the string.
   
   xLv)    accountSumMaxResults               -   Integer value to be used as 'max-results' while listing a AccountSummaries with optional parameters. (Make sure to use  '1' or '2').
   xLvi)   accountSumStartIndex               -   Integer value to be used as 'Start-index' while listing a AccountSummaries with optional parameters. (Make sure to use  '1' or '2').
   
   xLvii)  accountMaxResults                  -   Integer value to be used as 'max-results' while listing a Accounts with optional parameters.(Make sure to use  '1' or '2'). 
   xLviii) accountStartIndex                  -   Integer value to be used as 'Start-index' while listing a Accounts with optional parameters.(Make sure to use  '1' or '2').
   
   xLix)   segmentMaxResults                  -   Integer value to be used as 'max-results' while listing a Segments with optional parameters. (Make sure to use  '1' or '2').
   L)      segmentStartIndex                  -   Integer value to be used as 'Start-index' while listing a Segments with optional parameters.(Make sure to use  '1' or '2').
   
   Li)     customDataSourceMaxResults         -   Integer value to be used as 'max-results' while listing a CustomDataSources with optional parameters.(Make sure to use  '1' or '2').
   Lii)    customDataSourceStartIndex         -   Integer value to be used as 'Start-index' while listing a CustomDataSources with optional parameters.(Make sure to use  '1' or '2').
   
   Liii)   adWordsLinkMaxResults              -   Integer value to be used as 'max-results' while listing a AdWordsLink with optional parameters.(Make sure to use  '1' or '2').
   Liv)    adWordsLinkStartIndex              -   Integer value to be used as 'Start-index' while listing a AdWordsLink with optional parameters.(Make sure to use  '1' or '2').
   Lv)     adWordsLinkName                    -   Text to be used as 'Name' which is a mandatory parameter while creating a AdWordsLink.
   Lvi)    adWordsLinkCustomerId              -   Use the customer ID obtained under Step 6 ii. 
   Lvii)   adWordsLinkUpdatedName             -   Text to be used as 'Name' while updating a AdWordsLink with mandatory parameters.
   Lviii)  adWordsLinkAutoTaggingEnabled      -   Text to be used as 'adWordsLinkAutoTaggingEnabled' while updating a AdWordsLink with optional parameters. (Make sure to use a boolean value).
   Lix)    adWordsLinkPatchName               -   Text to be used as 'Name' while patch_updating a AdWordsLink with optional parameters.
   Lx)     adWordsLinkPatchAutoTaggingEnabled -   Text to be used as 'adWordsLinkAutoTaggingEnabled' while patch_updating a AdWordsLink with optional parameters. (Make sure to use a boolean value).
   
   Lxi)    experimentMaxResults               -   Integer value to be used as 'max-results' while listing a Experiment with optional parameters.(Make sure to use  '1' or '2').
   Lxii)   experimentStartIndex               -   Integer value to be used as 'Start-index' while listing a Experiment with optional parameters.(Make sure to use  '1' or '2').
   lxiii)  experimentProfileId                -   Use the view ID obtained under Step 6 i) c).
   Lxiv)   experimentName                     -   Text to be used as 'Name' which is a mandatory parameter while creating an experiment.
   Lxv)    experimentStatus                   -   Text to be used as 'Status' which is a mandatory parameter while creating an Experiment.(Make sure to use the value 'DRAFT' as the status).
   Lxvi)   experimentVariationName1           -   Text to be used as 'VariationName1' which is a mandatory parameter while creating an experiment.
   Lxvii)  experimentVariationUrl1            -   Text to be used as 'VariationUrl1' which is a optional parameter while creating an experiment.
   Lxviii) experimentVariationName2           -   Text to be used as 'VariationName2' which is a mandatory parameter while creating an experiment.
   Lxix)   experimentVariationUrl2            -   Text to be used as 'VariationUrl2' which is a optional parameter while creating an experiment.
   Lxx)    experimentEditableInGaUi           -   Text to be used as 'editableInGaUi' while updating an experiment with optional parameters.(Make sure to use a boolean value).
   Lxxi)   experimentDescription              -   Text to be used as 'description' while updating an experiment with optional parameters.
   Lxxii)  experimentUpdatedName              -   Text to be used as 'Name' while updating an experiment with mandatory parameters.
   Lxxiii) experimentUpdatedStatus            -   Text to be used as 'Status' while updating an experiment with mandatory parameters.(Make sure to use the value 'RUNNING' as the status).
   Lxxiv)  experimentUpdatedDescription       -   Text to be used as 'Description' while updating an experiment with mandatory parameters.
   Lxxv)   experimentUpdatedVariationName     -   Text to be used as 'VariationName' while updating an experiment with optional parameters.
   Lxxvi)  experimentUpdatdEditableInGaUi    -    Text to be used as 'editableInGaUi' while updating an experiment with optional parameters. (Make sure to use a boolean value).

   Note  :  Make sure to delete the adWords link after each test execution.

 8. Make sure that GoogleAnalytics is specified as a module in ESB Connector Parent pom.

    <module>googleanalytics/googleanalytics-connector/googleanalytics-connector-1.0.0/org.wso2.carbon.connector</module>   
 
 9. Navigate to "<ESB_CONNECTOR_HOME>/" and run the following command.
     $ mvn clean install
