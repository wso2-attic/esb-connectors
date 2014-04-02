STEPS:

1. Copy the "integration-base" folder into "Integration_Testing/products/esb/4.8.1/modules/integration/"

2. Using terminal, navigate to "Integration_Testing/products/esb/4.8.1/modules/integration/integration-base" and run following command.
    $mvn clean install

3. Add following dependancy to the file "Integration_Testing/products/esb/4.8.1/modules/integration/connectors/pom.xml"
	<dependency>

		<groupId>org.wso2.esb</groupId>

		<artifactId>org.wso2.connector.integration.test.base</artifactId>

		<version>4.8.1</version>

		<scope>system</scope>

		<systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>

	</dependency>

4. In your request files, use following syntax for parameterized values.
	%s(key_name_in_properties_file)

Ex :
    {

	"accessToken":"%s(accessToken)",

	"apiKey":"%s(apiKey)",

	"pageSize" : 50,

	"startDate" : "2014-03-05 11:43:01",

	"title" : "Firs",

	"isOrderAscending" : true,

	"fields" : ["title", "date_created", "question_count"]

    }

5. Use the same location for palce the esb request files and direct api request files.

Ex : Integration_Testing/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/restRequests/surveymonkey/

6. Use a single proxy file for the integration tests. The name of the proxy should be connector name.