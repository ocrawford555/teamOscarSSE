/**
 * Part of the HTTP Server
 * Thrown for incorrectly formatted HTTPHeaders
 */

package uk.ac.cam.teamOscarSSE;

public class HTTPHeaderException extends Exception {
	
	public HTTPHeaderException(String message) {
		super(message);
	}
}
