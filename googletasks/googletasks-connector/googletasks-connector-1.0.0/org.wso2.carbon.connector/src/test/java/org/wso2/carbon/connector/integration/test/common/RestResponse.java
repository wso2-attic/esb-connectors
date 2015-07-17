package org.wso2.carbon.connector.integration.test.common;

import org.json.JSONObject;
/**
 * 
 * @author madhawa
 * Response object
 */
public class RestResponse {
	private int responseCode;
	private JSONObject body;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public JSONObject getBody() {
		return body;
	}
	public void setBody(JSONObject body) {
		this.body = body;
	}
}
