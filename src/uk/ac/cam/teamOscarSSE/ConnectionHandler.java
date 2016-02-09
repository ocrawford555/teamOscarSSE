/**
 * Part of the HTTPServer.
 * Handles each individual connection from the HTTPServer
 */

package uk.ac.cam.teamOscarSSE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ConnectionHandler {
	
	/**
	 * Takes the specified connection and reads the HTTP header and body.
	 * Returns as a HTTPDetails object
	 * @param s
	 * @return
	 * @throws HTTPHeaderException 
	 */
	private HTTPDetails readInput(Socket s) throws HTTPHeaderException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			System.err.println("Error getting connection in Streams:");
			e.printStackTrace();
		}

		//Use StringBuilder to read in the input header
		String line;
		List<String> headers = new LinkedList<String>();
		StringBuilder body = null;
		try {
			while ((line=in.readLine())!= null) {
				if (line.isEmpty() && body == null) {
					body = new StringBuilder();
					continue;
				}
				if (body != null) {
					body.append(line).append('\n');
				} else {
					headers.add(line);
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading connection Input:");
			e.printStackTrace();
			System.err.println("Closing Connection");
			try {
				s.close();
			} catch (IOException e1) {
				System.err.println("Error closing user connection:");
				e1.printStackTrace();
			}
			return null;
		}
		
		HTTPDetails request = new HTTPDetails(headers, body);
		return request;
	}
	
	
	public ConnectionHandler(final Exchange exchange, final Socket s) {
		Thread handle = new Thread() {
			public void run() {
				try {
					//Get request details
					HTTPDetails requestDetails = readInput(s);
					
					//Send requestDetails to UserProcessor
					HTTPReturnMessage output = 
							UserProcessor.Process(exchange, requestDetails);
					
					//Write result back to the socket
					PrintWriter out = new PrintWriter(s.getOutputStream());
					out.print(output.toString());
					out.flush();
					out.close();
				} catch (HTTPHeaderException e1) {
					//TODO: Send failure message
					System.err.println("Malformed HTTP Header");
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("Error getting socket outputstream");
					e.printStackTrace();
				} finally {
					// No matter what, we need to close the connection
					try{
					s.close();
					} catch (IOException e) {
						System.err.println("Error closing user connection:");
						e.printStackTrace();
					}
				}
			};
		};
		handle.setDaemon(true);
		handle.start();
	}
}
