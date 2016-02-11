package uk.ac.cam.teamOscarSSE.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.HTTP;
import org.json.JSONObject;

import uk.ac.cam.teamOscarSSE.Exchange;
import uk.ac.cam.teamOscarSSE.HTTPServer;
import uk.ac.cam.teamOscarSSE.Stock;

public class ServerTester {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		//Make and start the server		
		Thread server = new Thread() {
			public void run() {
				List<Stock> stocks = new ArrayList<Stock>();
				stocks.add(new Stock("BAML", "Bank America", 100, 0.2f, 23054, 2000));
				Exchange exchange = new Exchange(stocks);
				HTTPServer testserver = new HTTPServer(8080, exchange);
				testserver.start();
			}
		};
		server.setDaemon(true);
		server.start();
		TimeUnit.SECONDS.sleep(1);
		
		//Send a demo request to it
		Map<String, String> requestheaders = new HashMap<String, String>();
		requestheaders.put("Method", "GET");
		requestheaders.put("HTTP-Version", "HTTP/1.1");
		requestheaders.put("Request-URI", "/stock/BAML");
		
		JSONObject requestjson = new JSONObject(requestheaders);
		String requestheader = HTTP.toString(requestjson);
		
		final Socket s = new Socket("localhost", 8080);
		OutputStream socketoutput = s.getOutputStream();
		PrintWriter writer = new PrintWriter(socketoutput);

		Thread reader = new Thread() {
			public void run() {
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
				}
				
				System.out.println("Response Recieved");
				for (String header : headers) {
					System.out.println(header);
				}			
				System.out.println(body.toString());
			}
		};
				
		reader.start();
		
		//Send header
		System.out.println("Sending Header");
		writer.print(requestheader);
		
		TimeUnit.SECONDS.sleep(30);
		s.close();
	}
}