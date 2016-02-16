package uk.ac.cam.teamOscarSSE;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.MalformedInputException;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class NewServer {
	
	private static void debugPrint (String string) {
		boolean DEBUG_MODE = false;
		if (DEBUG_MODE) {
			System.out.println(string);
		}
	}
	
	public static void start(int port, final Exchange stockExchange) throws IOException {
		int maximumClients = 32;
		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), maximumClients);
		HttpHandler handler = new HttpHandler() {
			
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				// Read request
				System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI());
				debugPrint("");
				debugPrint("Headers\n=======");
				Headers headers = exchange.getRequestHeaders();
				for (Map.Entry<String, List<String>> header : headers.entrySet()) {
					debugPrint(header.getKey() + ": " + header.getValue());
				}
				debugPrint("");
				debugPrint("Body\n====");
				InputStream inputStream = exchange.getRequestBody();
				StringBuilder builder = new StringBuilder();
				int nextByte;
				while ((nextByte = inputStream.read()) != -1) {
					builder.append(nextByte);
				}
				debugPrint("");
				inputStream.close();
				
				// Send response
				PrintWriter writer = null;
				try {
					String response = UserProcessor.processRequest(stockExchange, exchange.getRequestURI(), headers, builder.toString());
					if (response == null) {
						exchange.sendResponseHeaders(404, 0);
						writer = new PrintWriter(exchange.getResponseBody());
					} else {
						exchange.sendResponseHeaders(200, response.getBytes().length);
						writer = new PrintWriter(exchange.getResponseBody());
						writer.write(response);
					}
				} catch (MalformedInputException exception) {
					debugPrint("The request was malformed in some way and could not be processed.");
					exchange.sendResponseHeaders(500, 0);
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
			}
		};
		server.createContext("/", handler);
		server.start();
	}

}