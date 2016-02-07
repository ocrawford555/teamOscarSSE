package uk.ac.cam.teamOscarSSE;

public class HTTPReturnMessage {
	private String header;
	private String data;
	
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
