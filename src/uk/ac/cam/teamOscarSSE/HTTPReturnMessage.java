package uk.ac.cam.teamOscarSSE;

import java.util.HashMap;
import java.util.Map;

import org.json.HTTP;
import org.json.JSONObject;

public class HTTPReturnMessage {
	private String header;
	private String data;
	
	
	/**
	 * A response header must contain
     * {
     *    "HTTP-Version": "HTTP/1.1" (for example),
     *    "Status-Code": "200" (for example),
     *    "Reason-Phrase": "OK" (for example)
     * }
     * 
     * Use the org.json.HTTP.java code to convert a map into header data.
     * The data String should ideally be in a JSON format, but all depends on
     * the response as specified in the API.
     */
	public HTTPReturnMessage(String header, String data) {
		this.data = data;
		this.header = header;
	}
	
	public HTTPReturnMessage(Map<?, ?> data) {
		this(null, new JSONObject(data).toString());
		Map<String, String> resultHeaderMap =
				new HashMap<String, String>();
		resultHeaderMap.put("Status-Code", "200");
		resultHeaderMap.put("HTTP_Version", "HTTP/1.1");
		resultHeaderMap.put("Reason-Phrase", "OK");
		this.header = HTTP.toString(new JSONObject(resultHeaderMap));
	}
	
	public String getHeader() {
		return header;
	}
	
	public String getData() {
		return data;
	}
	
	public String toString() {
		//Convert to a whole HTTPReponse
		//TODO
		return header + data;
	}
}
