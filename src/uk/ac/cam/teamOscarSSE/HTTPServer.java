/**
 * Listens for incoming connections. Spawns a new ConnectionHandler for each
 * one. The ConnectionHandler should spawn a new thread so the HTTPServer can
 * listen for a new connection as quickly as possible.
 */

package uk.ac.cam.teamOscarSSE;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {

	ServerSocket ss = null;

	/**
	 * Set up the ServerSocket, bound to the specified port.
	 * Does not start accepting connections.
	 */
	public HTTPServer(int port) {
		//Create server socket
		try {
			System.out.println("Opening Connection on " + port);
			ss = new ServerSocket(port);
		} catch (IllegalArgumentException | IOException e) {
			System.err.println("Error Creating Server Socket on port " +
					port + ":");
			e.printStackTrace();
		}
	}


	/**
	 * Runs the loop that accepts HTTP Requests
     */
	public void start() {
		while(true) {
			Socket socket = null;
			try {
				// Wait for a client to connect.
				socket = ss.accept();
				
				//Start a new thread to deal with the request
				//TODO
			} catch (IOException e) {
				System.err.println("Error Accepting Connection:");
				e.printStackTrace();
			}
		}
	}
}
