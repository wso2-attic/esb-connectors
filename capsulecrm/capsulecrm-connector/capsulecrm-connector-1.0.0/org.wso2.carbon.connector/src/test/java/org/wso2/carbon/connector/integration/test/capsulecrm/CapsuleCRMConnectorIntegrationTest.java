/*
*  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.connector.integration.test.capsulecrm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.Base64;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.xml.sax.SAXException;

public class CapsuleCRMConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiUrl;
      
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("capsulecrm-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
        esbRequestHeadersMap.put("Accept", "application/xml");
        
        // Create base64-encoded auth string using apiToken and password
        final String authString = connectorProperties.getProperty("apiToken") + ":x";
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
       
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);  
        
        apiUrl=connectorProperties.getProperty("apiUrl")+"/api";
    }
    
    /**
     * Positive test case for listParties method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {listParties} integration test with mandatory parameters.")
    public void testListPartiesWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listParties");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listParties_mandatory.xml");
        
        final String xpathBase="//parties";
        
        final int esbPartiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", esbRestResponse.getBody()));
        final int esbPersonsCount=Integer.parseInt(getValueByExpression("count(//parties/person)", esbRestResponse.getBody()));
        final int esbOrganisationsCount=Integer.parseInt(getValueByExpression("count(//parties/organisation)", esbRestResponse.getBody()));
       
        String apiEndPoint = apiUrl + "/party";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final int apiPartiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", apiRestResponse.getBody()));
        final int apiPersonsCount=Integer.parseInt(getValueByExpression("count(//parties/person)", apiRestResponse.getBody()));
        final int apiOrganisationsCount=Integer.parseInt(getValueByExpression("count(//parties/organisation)", apiRestResponse.getBody()));
        
        Assert.assertEquals(esbPartiesCount, apiPartiesCount);
        
        if(esbPersonsCount==0){
        	Assert.fail("Pre-requisite does not matched. Please add one or more person.");
        }
       
        Assert.assertEquals(esbPersonsCount, apiPersonsCount);
        Assert.assertEquals(getValueByExpression(xpathBase+"/person[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/person[1]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/person[1]/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/person[1]/createdOn", apiRestResponse.getBody()));
        
        if(esbOrganisationsCount==0){
        	Assert.fail("Pre-requisite does not matched. Please add one or more organization.");
        }
        
        Assert.assertEquals(esbOrganisationsCount, apiOrganisationsCount);
        Assert.assertEquals(getValueByExpression(xpathBase+"/organisation[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/organisation[1]/id", apiRestResponse.getBody()));
        final String organizationId=getValueByExpression(xpathBase+"/organisation[1]/id", esbRestResponse.getBody());
        connectorProperties.setProperty("partyId", organizationId);
        Assert.assertEquals(getValueByExpression(xpathBase+"/organisation[1]/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/organisation[1]/name", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getParty method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testListPartiesWithMandatoryParameters"}, description = "capsulecrm {getParty} integration test with mandatory parameters.")
    public void testGetPartyWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:getParty");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getParty_mandatory.xml");
        
        final String xpathBase="//organisation"; 
        
        final String apiEndPoint = apiUrl + "/party/"+connectorProperties.getProperty("partyId");
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(getValueByExpression(xpathBase+"/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/createdOn", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getParty method.
     * @throws XMLStreamException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {getParty} integration test with negative case.")
    public void testGetPartyWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException{
    	
    	esbRequestHeadersMap.put("Action", "urn:getParty");
    	
    	RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getParty_negative.xml");
        
        final String apiEndPoint = apiUrl + "/party/INVALID";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for addOpportunity method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testListPartiesWithMandatoryParameters"}, description = "capsulecrm {addOpportunity} integration test with mandatory parameters.")
    public void testAddOpportunityWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:addOpportunity");
        
        connectorProperties.setProperty("milestone", "Bid");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addOpportunity_mandatory.xml");
       
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String xpathBase="//opportunity"; 
       
        String opportunityLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        opportunityLocationURL = opportunityLocationURL.substring(1, opportunityLocationURL.length() - 1);
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(opportunityLocationURL, "GET", apiRequestHeadersMap);
        
      
        Assert.assertEquals(getValueByExpression(xpathBase+"/name", apiRestResponse.getBody()), connectorProperties.getProperty("opportunityNameMand"));
        Assert.assertEquals(getValueByExpression(xpathBase+"/milestone", apiRestResponse.getBody()), connectorProperties.getProperty("milestone"));
    }
    
    /**
     * Positive test case for addOpportunity method with optional parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testListPartiesWithMandatoryParameters"}, description = "capsulecrm {addOpportunity} integration test with optional parameters.")
    public void testAddOpportunityWithOptionalParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:addOpportunity");
        
        connectorProperties.setProperty("milestone", "Won");
        connectorProperties.setProperty("durationBasis", "MONTH");
        connectorProperties.setProperty("duration", "3");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addOpportunity_optional.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
       
        final String xpathBase="//opportunity"; 
        
        String opportunityLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        opportunityLocationURL = opportunityLocationURL.substring(1, opportunityLocationURL.length() - 1);
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(opportunityLocationURL, "GET", apiRequestHeadersMap);
        
        final String opportunityId=getValueByExpression(xpathBase+"/id", apiRestResponse.getBody());
        connectorProperties.setProperty("opportunityId", opportunityId);
        
        Assert.assertEquals(getValueByExpression(xpathBase+"/durationBasis", apiRestResponse.getBody()), connectorProperties.getProperty("durationBasis"));
        Assert.assertEquals(getValueByExpression(xpathBase+"/currency", apiRestResponse.getBody()), connectorProperties.getProperty("currency"));
        Assert.assertEquals(getValueByExpression(xpathBase+"/duration", apiRestResponse.getBody()), connectorProperties.getProperty("duration"));
        Assert.assertEquals(getValueByExpression(xpathBase+"/description", apiRestResponse.getBody()), connectorProperties.getProperty("OpportunityDescription"));
        Assert.assertEquals(getValueByExpression(xpathBase+"/owner", apiRestResponse.getBody()), connectorProperties.getProperty("owner"));
    }
    
    /**
     * Negative test case for addOpportunity method.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testListPartiesWithMandatoryParameters"}, description = "capsulecrm {addOpportunity} integration test with negative case.")
    public void testAddOpportunityWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:addOpportunity");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addOpportunity_negative.xml");
        
        final String apiEndPoint = apiUrl + "/party/"+connectorProperties.getProperty("partyId")+"/opportunity"; 
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,"api_addOpportunity_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getOpportunity method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddOpportunityWithOptionalParameters"}, description = "capsulecrm {getOpportunity} integration test with mandatory parameters.")
    public void testGetOpportunityWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:getOpportunity");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOpportunity_mandatory.xml");
        
        final String xpathBase="//opportunity"; 
        
        final String apiEndPoint = apiUrl + "/opportunity/"+connectorProperties.getProperty("opportunityId");
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(getValueByExpression(xpathBase+"/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/milestone", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/milestone", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/currency", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/currency", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/duration", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/duration", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/createdOn", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getOpportunity method.
     * @throws XMLStreamException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {getOpportunity} integration test with negative case.")
    public void testGetOpportunityWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException{
    	
    	esbRequestHeadersMap.put("Action", "urn:getOpportunity");
    	
    	RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOpportunity_negative.xml");
        
        final String apiEndPoint = apiUrl + "/opportunity/INVALID";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listOpportunities method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddOpportunityWithMandatoryParameters", "testAddOpportunityWithOptionalParameters"}, description = "capsulecrm {listOpportunities} integration test with mandatory parameters.")
    public void testListOpportunitiesWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listOpportunities");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOpportunities_mandatory.xml");
        
        final String xpathBase="//opportunities"; 
        
        final int esbOpportunitiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", esbRestResponse.getBody()));
       
        String apiEndPoint = apiUrl + "/opportunity";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final int apiOpportunitiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", apiRestResponse.getBody()));

        Assert.assertEquals(esbOpportunitiesCount, apiOpportunitiesCount);
               
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/partyId", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/partyId", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/milestone", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/milestone", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/createdOn", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listOpportunities method with optional parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddOpportunityWithMandatoryParameters", "testAddOpportunityWithOptionalParameters"}, description = "capsulecrm {listOpportunities} integration test with optional parameters.")
    public void testListOpportunitiesWithOptionalParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listOpportunities");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOpportunities_optional.xml");
        
        final String xpathBase="//opportunities"; 
        
        final int esbOpportunitiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", esbRestResponse.getBody()));
       
        String apiEndPoint = apiUrl + "/opportunity?limit=1&milestone=Won&start=0";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final int apiOpportunitiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", apiRestResponse.getBody()));

        Assert.assertEquals(esbOpportunitiesCount, apiOpportunitiesCount);
               
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/milestone", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/milestone", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/createdOn", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/expectedCloseDate", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/expectedCloseDate", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listOpportunities method.
     * @throws XMLStreamException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {listOpportunities} integration test with negative case.")
    public void testListOpportunitiesWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException{
    	
    	esbRequestHeadersMap.put("Action", "urn:listOpportunities");
    	
    	RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOpportunities_negative.xml");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/opportunity?milestone=INVALID";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listOpportunitiesByParty method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddOpportunityWithMandatoryParameters", "testAddOpportunityWithOptionalParameters"}, description = "capsulecrm {listOpportunitiesByParty} integration test with mandatory parameters.")
    public void testListOpportunitiesByPartyWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listOpportunitiesByParty");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOpportunitiesByParty_mandatory.xml");
        
        final String xpathBase="//opportunities";
        final int esbOpportunitiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", esbRestResponse.getBody()));
       
        String apiEndPoint = apiUrl + "/party/"+connectorProperties.getProperty("partyId")+"/opportunity";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final int apiOpportunitiesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", apiRestResponse.getBody()));
        
        Assert.assertEquals(esbOpportunitiesCount, apiOpportunitiesCount);
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/partyId", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/partyId", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/milestone", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/milestone", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/opportunity[1]/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/opportunity[1]/createdOn", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listOpportunitiesByParty method.
     * @throws XMLStreamException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {listOpportunitiesByParty} integration test with negative case.")
    public void testListOpportunitiesByPartyWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException{
    	
    	esbRequestHeadersMap.put("Action", "urn:listOpportunitiesByParty");
    	
    	RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOpportunitiesByParty_negative.xml");
        
        final String apiEndPoint = apiUrl + "/party/INVALID/opportunity";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for addCase method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testListPartiesWithMandatoryParameters"}, description = "capsulecrm {addCase} integration test with mandatory parameters.")
    public void testAddCaseWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:addCase");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCase_mandatory.xml");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String xpathBase="//kase"; 
       
        String caseLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        caseLocationURL = caseLocationURL.substring(1, caseLocationURL.length() - 1);
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(caseLocationURL, "GET", apiRequestHeadersMap);

        Assert.assertEquals(getValueByExpression(xpathBase+"/name", apiRestResponse.getBody()), connectorProperties.getProperty("caseNameMand"));
        Assert.assertEquals(getValueByExpression(xpathBase+"/partyId", apiRestResponse.getBody()), connectorProperties.getProperty("partyId"));
    }
    
    /**
     * Positive test case for addCase method with optional parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testListPartiesWithMandatoryParameters"}, description = "capsulecrm {addCase} integration test with optional parameters.")
    public void testAddCaseWithOptionalParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:addCase");
        
        connectorProperties.setProperty("caseStatus", "closed");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCase_optional.xml");
       
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String xpathBase="//kase"; 
        String caseLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        caseLocationURL = caseLocationURL.substring(1, caseLocationURL.length() - 1);
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(caseLocationURL, "GET", apiRequestHeadersMap);
        final String caseId=getValueByExpression(xpathBase+"/id", apiRestResponse.getBody());
        connectorProperties.setProperty("caseId", caseId);
        final String apiStatus=getValueByExpression(xpathBase+"/status", apiRestResponse.getBody()).toLowerCase();
        
        Assert.assertEquals(getValueByExpression(xpathBase+"/description", apiRestResponse.getBody()), connectorProperties.getProperty("caseDescription"));
        Assert.assertEquals(apiStatus, connectorProperties.getProperty("caseStatus"));
        Assert.assertEquals(getValueByExpression(xpathBase+"/owner", apiRestResponse.getBody()), connectorProperties.getProperty("owner"));
    }
    
    /**
     * Negative test case for addCase method.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testListPartiesWithMandatoryParameters"}, description = "capsulecrm {addCase} integration test with negative case.")
    public void testAddCaseWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:addCase");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addCase_negative.xml");
        
        final String apiEndPoint = apiUrl + "/party/"+connectorProperties.getProperty("partyId")+"/kase"; 
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,"api_addCase_negative.xml");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for getCase method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddOpportunityWithOptionalParameters"}, description = "capsulecrm {getCase} integration test with mandatory parameters.")
    public void testGetCaseWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:getCase");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCase_mandatory.xml");
        
        final String xpathBase="//kase"; 
        final String apiEndPoint = apiUrl + "/kase/"+connectorProperties.getProperty("caseId");
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(getValueByExpression(xpathBase+"/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/description", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/description", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/partyId", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/partyId", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/owner", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/owner", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/createdOn", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for getCase method.
     * @throws XMLStreamException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {getCase} integration test with negative case.")
    public void testGetCaseWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException{
    	
    	esbRequestHeadersMap.put("Action", "urn:getCase");
    	
    	RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCase_negative.xml");
        
        final String apiEndPoint = apiUrl + "/kase/INVALID";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listCases method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddCaseWithMandatoryParameters", "testAddCaseWithOptionalParameters"}, description = "capsulecrm {listCases} integration test with mandatory parameters.")
    public void testListCasesWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listCases");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCases_mandatory.xml");
        
        final String xpathBase="//kases"; 
        
        final int esbCasesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", esbRestResponse.getBody()));

        String apiEndPoint = apiUrl + "/kase";
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final int apiCasesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", apiRestResponse.getBody()));

        Assert.assertEquals(esbCasesCount, apiCasesCount);      
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/partyId", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/partyId", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/createdOn", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/updatedOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/updatedOn", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listCases method with optional parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddCaseWithMandatoryParameters", "testAddCaseWithOptionalParameters"}, description = "capsulecrm {listCases} integration test with optional parameters.")
    public void testListCasesWithOptionalParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listCases");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCases_optional.xml");
        
        final String xpathBase="//kases"; 
        
        final int esbCasesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", esbRestResponse.getBody()));
       
        String apiEndPoint = apiUrl + "/kase?limit=1&start=0&status="+connectorProperties.getProperty("caseStatus");
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final int apiCasesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", apiRestResponse.getBody()));

        Assert.assertEquals(esbCasesCount, apiCasesCount);
               
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/partyId", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/partyId", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/status", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/status", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/createdOn", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listCases method.
     * @throws XMLStreamException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {listCases} integration test with negative case.")
    public void testListCasesWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException{
    	
    	esbRequestHeadersMap.put("Action", "urn:listCases");
    	
    	RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCases_negative.xml");
        
        final String apiEndPoint = apiUrl + "/kase?status=INVALID";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
    /**
     * Positive test case for listCasesByParty method with mandatory parameters.
     * @throws XMLStreamException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(dependsOnMethods={"testAddCaseWithMandatoryParameters", "testAddCaseWithOptionalParameters"}, description = "capsulecrm {listCasesByParty} integration test with mandatory parameters.")
    public void testListCasesByPartyWithMandatoryParameters() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException {
    
        esbRequestHeadersMap.put("Action", "urn:listCasesByParty");
        
        RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCasesByParty_mandatory.xml");
        
        final String xpathBase="//kases";
        
        final int esbCasesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", esbRestResponse.getBody()));
       
        String apiEndPoint = apiUrl + "/party/"+connectorProperties.getProperty("partyId")+"/kase";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final int apiCasesCount=Integer.parseInt(getValueByExpression(xpathBase+"/@size", apiRestResponse.getBody()));
        
        Assert.assertEquals(esbCasesCount, apiCasesCount);

        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/id", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/id", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/name", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/name", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/status", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/status", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/createdOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/createdOn", apiRestResponse.getBody()));
        Assert.assertEquals(getValueByExpression(xpathBase+"/kase[1]/updatedOn", esbRestResponse.getBody()), getValueByExpression(xpathBase+"/kase[1]/updatedOn", apiRestResponse.getBody()));
    }
    
    /**
     * Negative test case for listCasesByParty method.
     * @throws XMLStreamException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    @Test(description = "capsulecrm {listCasesByParty} integration test with negative case.")
    public void testListCasesByPartyWithNegativeCase() throws IOException, XMLStreamException, XPathExpressionException, SAXException, ParserConfigurationException{
    	
    	esbRequestHeadersMap.put("Action", "urn:listCasesByParty");
    	
    	RestResponse<OMElement> esbRestResponse =
                sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOpportunitiesByParty_negative.xml");
        
        final String apiEndPoint = apiUrl + "/party/INVALID/kase";
        
        RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(getValueByExpression("//response/message", esbRestResponse.getBody()), getValueByExpression("//response/message", apiRestResponse.getBody()));
    }
    
}
