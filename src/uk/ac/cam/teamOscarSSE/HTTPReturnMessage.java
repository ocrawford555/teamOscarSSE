package uk.ac.cam.teamOscarSSE;

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
