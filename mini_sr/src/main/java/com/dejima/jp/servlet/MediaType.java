/*
 * $Id: MediaType.java,v 1.3 2006/10/18 04:12:02 jun Exp $
 */

package com.dejima.jp.servlet;

import java.util.*;

/**
 * This class represents Internet Media Types in the Content-Type header field.
 * 
 * @verison $Revision: 1.3 $
 */
public class MediaType {
	/**
	 * The type.
	 */
	private String type;

	/**
	 * The sub type.
	 */
	private String subType;

	/**
	 * Parameters which are String attribute and String value pairs.
	 */
	private Map parameters;

	/**
	 * Create new media type from the specified string. The format of string is
	 * shown following. media-type = type "/" subtype *( ";" parameter ) type =
	 * token subtype = token
	 * 
	 * Parameters may follow the type/subtype in the form of attribute/value
	 * pairs. parameter = attribute "=" value attribute = token value = token |
	 * quoted-string
	 * 
	 */
	public MediaType(String str) throws IllegalArgumentException {
		parameters = new HashMap();
		parseMediaTypeString(str);
	}

	/**
	 * Parse the specified string.
	 */
	private void parseMediaTypeString(String str) throws IllegalArgumentException {
		int p1 = str.indexOf('/');
		if (p1 > 0 && p1 < str.length() - 1) {
			type = str.substring(0, p1).toLowerCase();
			int p2 = str.indexOf(';');
			if (p2 > p1 + 1) {
				subType = str.substring(p1 + 1, p2).trim().toLowerCase();
				parseParameters(str.substring(p2 + 1));
			} else {
				subType = str.substring(p1 + 1).trim().toLowerCase();
			}
		}
		if (type == null && subType == null) {
			throw new IllegalArgumentException("Invalid media type format: " + str);
		}
	}

	private void parseParameters(String str) {
		int p1 = str.indexOf('=');
		if (p1 > 0 && p1 < str.length() - 1) {
			String key = str.substring(0, p1).trim().toLowerCase();
			String value = str.substring(p1 + 1).trim().toLowerCase();
			parameters.put(key, value);
		}
	}

	/**
	 * Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the sub type.
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * Returns parameter names.
	 */
	public Set getParameterNames() {
		return parameters.keySet();
	}

	/**
	 * Returns the specified parameter.
	 */
	public String getParameter(String paramName) {
		return (String) parameters.get(paramName.toLowerCase());
	}

	/**
	 * Returns the string reprensetation of the object.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(type);
		buf.append('/');
		buf.append(subType);
		for (Iterator ite = parameters.entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			buf.append(";");
			buf.append(entry.getKey());
			buf.append('=');
			buf.append(entry.getValue());
		}

		return buf.toString();
	}
}
