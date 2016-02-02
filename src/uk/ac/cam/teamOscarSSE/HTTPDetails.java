/**
 * Part of the HTTP Server.
 * Contains the HTTP Header and Body of an individual HTTP Connection's request.
 */

package uk.ac.cam.teamOscarSSE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HTTPDetails {

	private final String method;
	private final String uri;
	private final String protocol;
	private final HashMap<String, String> requestHeaders;
	private final StringBuilder body;
	
	/**
	 * Each line of the headers (i.e. a new parameter on each line), will be
	 * parsed into Key:Value and stored in the HashMap, apart for the request
	 * line (e.g. "GET http://localhost:8080/Stock/BAML/ HTTP/1.1") which will
	 * be split into method (GET/POST), uri (http://...), protocol (HTTP/1.1)
	 * @param headers
	 * @param body
	 * @throws HTTPHeaderException
	 * 		If the supplied header is badly formed, then the constructor will
	 * 		fail to successfully parse it and will throw an exception.
	 */
	public HTTPDetails(List<String> headers, StringBuilder body)
			throws HTTPHeaderException {
		
		//Parse the headers
		Iterator<String> headersIter = headers.iterator();
		
		//Get the Method and URL
		if (!headersIter.hasNext()) {
			throw new HTTPHeaderException("Header is empty");
		}
		
		//TODO: Validate the request line
		String[] requestLine = headersIter.next().trim().split(" ");
		try {
			method = requestLine[0];
			uri = requestLine[1];
			protocol = requestLine[2];
		} catch (IndexOutOfBoundsException e) {
			throw new HTTPHeaderException(requestLine.length + "arguments in "
					+ "request line (expected 3: Method URI Protocol, space "
					+ "delimited)");
		}
		
		//Add all remaining parameters to the HashMap
		requestHeaders = new HashMap<String, String>();
		while(headersIter.hasNext()) {
			appendHeaderParam(headersIter.next());
		}
		
		//Set Body
		this.body = body;
	}

	
	/**
	 * Takes a line of HTTP header, splits it into the Key:Value pair
	 * and adds to the headers Hashtable.
	 * @param headerline
	 * @throws HTTPHeaderException
	 */
	private void appendHeaderParam(String headerline)
			throws HTTPHeaderException {
		
		int index = headerline.indexOf(':');
		if (index == -1) {
			//':' does not occur in the line: malformed header
			throw new HTTPHeaderException("Malformed Header: " + headerline);
		}
		
		requestHeaders.put(headerline.substring(0, index),
							headerline.substring(index+1, headerline.length()));
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getURI() {
		return uri;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	/**
	 * Get the value stored at key
	 * @param key
	 * 		key to get the value of.
	 * @return
	 * 		the String value. NULL if value is NULL or if key does not exist.
	 * 		If you get NULL, use headersContain(key) to check if the key
	 * 		actually exists
	 */
	public String getHeader(String key) {
		return requestHeaders.get(key);
	}
	
	/**
	 * Determine if the specified key exists within the hashmap.
	 * @param key
	 * 		key to search for.
	 * @return
	 * 		true if header exists (although not necessarily if a value exists)
	 * 		false if header does not exist.
	 */
	public boolean headersContain(String key) {
		return requestHeaders.containsKey(key);
	}
	
	/**
	 * @return
	 * 		A copy of the headers HashMap. (Copy to ensure the original is
	 * 		left untouched.) For this reason, you should attempt to use
	 * 		getHeader(key) and headersContain(key) for speed purposes.
	 */
	public HashMap<String, String> getHeaders() {
		return (HashMap<String, String>) requestHeaders.clone();
	}
	
	
}
