package uk.ac.cam.teamOscarSSE.testing;

import uk.ac.cam.teamOscarSSE.Exchange;
import uk.ac.cam.teamOscarSSE.NewServer;
import uk.ac.cam.teamOscarSSE.Stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestServer {
	public static void main(String[] args) {
		List<Stock> stocks = new ArrayList<Stock>();
		stocks.add(new Stock("BAML", "Bank America", 100, 0.2f, 23054, 2000));
		Exchange exchange = new Exchange();
		exchange.startRound(stocks, 30);
		try {
			NewServer.start(8080, exchange);
		} catch (IOException exception) {
			System.out.println("There was a problem starting the server:");
			exception.printStackTrace();
		}
	}
}
