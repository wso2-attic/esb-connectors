package org.wso2.carbon.connector.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilePattenMatcher {

	private Pattern pattern;
	private Matcher matcher;

	public FilePattenMatcher(String patternStr) {
		pattern = Pattern.compile(patternStr);
	}

	/**
	 * Validate file with regular expression
	 * 
	 * @param image
	 *            file for validation
	 * @return true valid image, false invalid image
	 */
	public boolean validate(final String image) {

		matcher = pattern.matcher(image);
		return matcher.matches();

	}
}