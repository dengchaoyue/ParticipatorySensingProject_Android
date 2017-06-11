/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.mappage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author dengchaoyue
 *
 */
class QueryString {
	private StringBuffer query = new StringBuffer();

	private synchronized void encode(String name, String value) {
		try {
			query.append(URLEncoder.encode(name, "UTF-8"));
			query.append('=');
			query.append(URLEncoder.encode(value, "UTF-8"));
			
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("Broken VM does not support UTF-8");
		}
	}

	
	public QueryString(String name, String value) {
		encode(name, value);
	}

	public synchronized void add(String name, String value) {
		query.append('&');
		encode(name, value);
	}
	public String getQuery() {
		return query.toString();
	}

	public String toString() {
		return getQuery();
	}
}
