package uk.ac.cam.teamOscarSSE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class NewServer {
	
	public static void main (String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 32);
		HttpHandler handler = new HttpHandler() {
			
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				// Read request
				System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + "\n");
				System.out.println("Headers\n=======");
				Headers headers = exchange.getRequestHeaders();
				for (Map.Entry<String, List<String>> header : headers.entrySet()) {
					System.out.println(header.getKey() + ": " + header.getValue());
				}
				System.out.println("");
				System.out.println("Body\n====");
				InputStream inputStream = exchange.getRequestBody();
				int nextByte;
				while ((nextByte = inputStream.read()) != -1) {
					System.out.print(nextByte);
				}
				System.out.println("");
				inputStream.close();
				
				// Send response
				exchange.sendResponseHeaders(200, 0);
				OutputStream outputStream = exchange.getResponseBody();
				outputStream.close();
			}
		};
		server.createContext("/", handler);
		server.start();
	}

}