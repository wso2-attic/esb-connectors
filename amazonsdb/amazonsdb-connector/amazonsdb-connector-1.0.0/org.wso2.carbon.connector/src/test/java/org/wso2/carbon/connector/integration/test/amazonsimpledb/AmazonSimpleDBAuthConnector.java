/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved. WSO2 Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.connector.integration.test.amazonsimpledb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class AmazonSimpleDBAuthConnector which helps to generate authentication
 * signature for Amazon SimpleDB WSO2 ESB Connector.
 */
public class AmazonSimpleDBAuthConnector {

	static Logger LOGGER = Logger.getLogger(AmazonSimpleDBAuthConnector.class.getName());
	/**
	 * Connect method which is generating authentication of the connector for
	 * each request.
	 * 
	 * @param messageContext
	 *            ESB messageContext.
	 */
	public final String getXFormUrl(final JSONObject signatureRequestObject) {

		final StringBuilder signatureBuilder = new StringBuilder();
		final StringBuilder xFormUrlBuilder = new StringBuilder();
		// Generate time-stamp which will be sent to API and used in Signature
		final TimeZone timeZone = TimeZone
				.getTimeZone(AmazonSimpleDBConstants.GMT);
		final DateFormat dateFormat = new SimpleDateFormat(
				AmazonSimpleDBConstants.DATE_FORMAT);
		dateFormat.setTimeZone(timeZone);
		final String timestamp = dateFormat.format(new Date());
		
		try {
		// Adding the timestamp to the jsonobject
		signatureRequestObject.put(AmazonSimpleDBConstants.TIMESTAMP,timestamp);
		final Map<String, String> parameterNamesMap = getParameterNamesMap();
		final Map<String, String> parametersMap = getSortedParametersMap(
				signatureRequestObject, parameterNamesMap);

			signatureBuilder.append(AmazonSimpleDBConstants.HTTP_METHOD);
			signatureBuilder.append(AmazonSimpleDBConstants.NEW_LINE);
			signatureBuilder.append(AmazonSimpleDBConstants.HOST);
			signatureBuilder.append(AmazonSimpleDBConstants.NEW_LINE);
			signatureBuilder.append(AmazonSimpleDBConstants.HTTP_REQUEST_URI);
			signatureBuilder.append(AmazonSimpleDBConstants.NEW_LINE);
			signatureBuilder.append(AmazonSimpleDBConstants.AWS_ACCESS_KEY_ID);
			signatureBuilder.append(AmazonSimpleDBConstants.EQUAL);
			signatureBuilder.append(signatureRequestObject
					.get(AmazonSimpleDBConstants.ACCESS_KEY_ID));

			final String charSet = Charset.defaultCharset().toString();

			xFormUrlBuilder.append(AmazonSimpleDBConstants.AWS_ACCESS_KEY_ID
					+ AmazonSimpleDBConstants.EQUAL);
			xFormUrlBuilder.append(signatureRequestObject
							.get(AmazonSimpleDBConstants.ACCESS_KEY_ID));

			final Set<String> keySet = parametersMap.keySet();
			for (String key : keySet) {
				final String param = AmazonSimpleDBConstants.AMPERSAND
						+ URLEncoder.encode(key, charSet)
						+ AmazonSimpleDBConstants.EQUAL
						+ URLEncoder.encode(parametersMap.get(key), charSet);
				signatureBuilder.append(param);

				xFormUrlBuilder.append(AmazonSimpleDBConstants.AMPERSAND);
				xFormUrlBuilder.append(URLEncoder.encode(key, charSet));
				xFormUrlBuilder.append(AmazonSimpleDBConstants.EQUAL);
				xFormUrlBuilder.append(URLEncoder.encode(parametersMap.get(key), charSet));
			}
			
			// Sign the created string.
			final AmazonSimpleDBAuthentication amazonSDBAuth = new AmazonSimpleDBAuthentication(
					(String) signatureRequestObject.get(AmazonSimpleDBConstants.SECRET_ACCESS_KEY));

			// '-' , '_' , '.' and '~' shouldn't be encoded as per the API
			// document and '*' should be encoded explicitly since java doesn't
			// encode
			final String authSignValue = amazonSDBAuth
					.getAuthorizationSignature(signatureBuilder
							.toString()
							.replace(AmazonSimpleDBConstants.PLUS,
									AmazonSimpleDBConstants.URL_ENCODED_PLUS)
							.replace(AmazonSimpleDBConstants.URL_ENCODED_TILT,
									AmazonSimpleDBConstants.TILT)
							.replace(
									AmazonSimpleDBConstants.ASTERISK,
									AmazonSimpleDBConstants.URL_ENCODED_ASTERISK));
			// Set signature in messageContext to be used in template
			xFormUrlBuilder.append(AmazonSimpleDBConstants.AMPERSAND);
			xFormUrlBuilder.append(URLEncoder.encode(AmazonSimpleDBConstants.API_SIGNATURE, charSet)
					+ AmazonSimpleDBConstants.EQUAL);
			xFormUrlBuilder.append(URLEncoder.encode(authSignValue, charSet));
			
			// Set x-www-form url in messageContext to be used in PayloadFactory
			
		} catch (InvalidKeyException ike) {
			LOGGER.error("Invalid key", ike);
		} catch (NoSuchAlgorithmException iae) {
			LOGGER.error("Invalid Algorithm", iae);
		} catch (UnsupportedEncodingException uee) {
			LOGGER.error("Encoding Not Supported", uee);
		} catch (Exception exc) {
			LOGGER.error("Error occured in connector", exc);
		}
		return xFormUrlBuilder
				.toString()
				.replace(AmazonSimpleDBConstants.PLUS,
						AmazonSimpleDBConstants.URL_ENCODED_PLUS)
				.replace(AmazonSimpleDBConstants.URL_ENCODED_TILT,
						AmazonSimpleDBConstants.TILT)
				.replace(AmazonSimpleDBConstants.ASTERISK,
						AmazonSimpleDBConstants.URL_ENCODED_ASTERISK);
	}

	/**
	 * getKeys method returns a list of parameter keys.
	 * 
	 * @return list of parameter key value.
	 */
	private String[] getParameterKeys() {
		return new String[] { AmazonSimpleDBConstants.MAX_NO_OF_DOMAINS,
				AmazonSimpleDBConstants.NEXT_TOKEN,
				AmazonSimpleDBConstants.ACTION,
				AmazonSimpleDBConstants.VERSION,
				AmazonSimpleDBConstants.SIGNATURE_VERSION,
				AmazonSimpleDBConstants.SIGNATURE_METHOD,
				AmazonSimpleDBConstants.TIMESTAMP,
				AmazonSimpleDBConstants.DOMAIN_NAME,
				AmazonSimpleDBConstants.EXPECTED_NAME,
				AmazonSimpleDBConstants.EXPECTED_VALUE,
				AmazonSimpleDBConstants.EXPECTED_EXISTS,
				AmazonSimpleDBConstants.ITEM_NAME,
				AmazonSimpleDBConstants.CONSISTENT_READ,
				AmazonSimpleDBConstants.SELECT_EXPRESSION

		};
	}

	/**
	 * getCollectionParameterKeys method returns a list of predefined parameter
	 * keys which users will be used. to send collection of values in each
	 * parameter.
	 * 
	 * @return list of parameter key value.
	 */
	private String[] getMultivaluedParameterKeys() {
		return new String[] { AmazonSimpleDBConstants.ATTRIBUTES,
				AmazonSimpleDBConstants.ATTRIBUTE_NAMES };
	}

	/**
	 * getParametersMap method used to return list of parameter values sorted by
	 * expected API parameter names.
	 * 
	 * @param messageContext
	 *            ESB messageContext.
	 * @param namesMap
	 *            contains a map of esb parameter names and matching API
	 *            parameter names
	 * @return assigned parameter values as a HashMap.
	 */
	private Map<String, String> getSortedParametersMap(
			final JSONObject jsonObject, final Map<String, String> namesMap) {

		final String[] singleValuedKeys = getParameterKeys();
		final Map<String, String> parametersMap = new TreeMap<String, String>();
		try {
			// Stores sorted, single valued API parameters
			for (byte index = 0; index < singleValuedKeys.length; index++) {
				final String key = singleValuedKeys[index];
				// builds the parameter map only if provided by the user
				if (jsonObject.has(key) && !("").equals((String) jsonObject.get(key))) {
					parametersMap.put(namesMap.get(key),
							(String) jsonObject.get(key));
				}
			}
			final String[] multiValuedKeys = getMultivaluedParameterKeys();
			// Stores sorted, multi-valued API parameters
			for (byte index = 0; index < multiValuedKeys.length; index++) {
				final String key = multiValuedKeys[index];
				// builds the parameter map only if provided by the user
				if (jsonObject.has(key) && !("").equals((String) jsonObject.get(key))) {
					final String collectionParam = (String) jsonObject.get(key);
					// Splits the collection parameter to retrieve parameters
					// separately
					final String[] keyValuepairs = collectionParam.split(AmazonSimpleDBConstants.AMPERSAND);
					for (String keyValue : keyValuepairs) {
						if (keyValue.contains(AmazonSimpleDBConstants.EQUAL) && keyValue.split(AmazonSimpleDBConstants.EQUAL).length == AmazonSimpleDBConstants.TWO) {
							// Split the key and value of parameters to be sent
							// to API
							parametersMap.put(keyValue.split(AmazonSimpleDBConstants.EQUAL)[0], keyValue.split(AmazonSimpleDBConstants.EQUAL)[1]);
						} else {
							LOGGER.error("Invalid Parameter Value");
						}
					}
				}

			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return parametersMap;
	}

	/**
	 * getparameterNamesMap returns a map of esb parameter names and
	 * corresponding API parameter names.
	 * 
	 * @return generated map.
	 */
	private Map<String, String> getParameterNamesMap() {

		final Map<String, String> map = new HashMap<String, String>();
		map.put(AmazonSimpleDBConstants.MAX_NO_OF_DOMAINS,
				AmazonSimpleDBConstants.API_MAX_NO_OF_DOMAINS);
		map.put(AmazonSimpleDBConstants.NEXT_TOKEN,
				AmazonSimpleDBConstants.API_NEXT_TOKEN);
		map.put(AmazonSimpleDBConstants.ACTION,
				AmazonSimpleDBConstants.API_ACTION);
		map.put(AmazonSimpleDBConstants.VERSION,
				AmazonSimpleDBConstants.API_VERSION);
		map.put(AmazonSimpleDBConstants.SIGNATURE_VERSION,
				AmazonSimpleDBConstants.API_SIGNATURE_VERSION);
		map.put(AmazonSimpleDBConstants.SIGNATURE_METHOD,
				AmazonSimpleDBConstants.API_SIGNATURE_METHOD);
		map.put(AmazonSimpleDBConstants.TIMESTAMP,
				AmazonSimpleDBConstants.API_TIMESTAMP);
		map.put(AmazonSimpleDBConstants.DOMAIN_NAME,
				AmazonSimpleDBConstants.API_DOMAIN_NAME);
		map.put(AmazonSimpleDBConstants.EXPECTED_NAME,
				AmazonSimpleDBConstants.API_EXPECTED_NAME);
		map.put(AmazonSimpleDBConstants.EXPECTED_VALUE,
				AmazonSimpleDBConstants.API_EXPECTED_VALUE);
		map.put(AmazonSimpleDBConstants.EXPECTED_EXISTS,
				AmazonSimpleDBConstants.API_EXPECTED_EXISTS);
		map.put(AmazonSimpleDBConstants.ITEM_NAME,
				AmazonSimpleDBConstants.API_ITEM_NAME);
		map.put(AmazonSimpleDBConstants.CONSISTENT_READ,
				AmazonSimpleDBConstants.API_CONSISTENT_READ);
		map.put(AmazonSimpleDBConstants.SELECT_EXPRESSION,
				AmazonSimpleDBConstants.API_SELECT_EXPRESSION);
		return map;
	}

}
