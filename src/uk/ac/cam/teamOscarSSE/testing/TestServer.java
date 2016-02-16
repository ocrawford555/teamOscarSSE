package uk.ac.cam.teamOscarSSE.testing;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamOscarSSE.Exchange;
import uk.ac.cam.teamOscarSSE.HTTPServer;
import uk.ac.cam.teamOscarSSE.Stock;

public class TestServer {
	public static void main(String[] args) {
		List<Stock> stocks = new ArrayList<Stock>();
		stocks.add(new Stock("BAML", "Bank America", 100, 0.2f, 23054, 2000));
		Exchange exchange = new Exchange(stocks);
		HTTPServer testserver = new HTTPServer(8080, exchange);
		testserver.start();
	}
}