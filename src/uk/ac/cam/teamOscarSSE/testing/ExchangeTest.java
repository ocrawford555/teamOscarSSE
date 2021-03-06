package uk.ac.cam.teamOscarSSE.testing;

import uk.ac.cam.teamOscarSSE.server.*;

import java.util.ArrayList;

public class ExchangeTest {

	public static void voidExchange() {
		ArrayList<Stock> stocks = new ArrayList<>();
		stocks.add(new Stock("BP", "British Petroleum", 5000, 0.2f, 1000, 500));

		ArrayList<Player> players = new ArrayList<>();
		players.add(new Player("Alice", "AA"));
		players.add(new Player("Bob", "BB"));
		LeaderBoard lb = new LeaderBoard(players);
		Exchange exchange = new Exchange();

		for (Player player : players) {
			exchange.addPlayer(player);
		}
		lb.get();
		exchange.startRound(stocks);
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(exchange.getUptimeFormatted());

		exchange.endRound();
		for (Stock stock : stocks) {
			System.out.println(stock.getSymbol() + " " + stock.getStockPrice());
		}
		lb.get();
	}

	public static void singleTradeExchange() {
		ArrayList<Stock> stocks = new ArrayList<>();
		stocks.add(new Stock("BP", "British Petroleum", 5000, 0.2f, 1000, 500));

		ArrayList<Player> players = new ArrayList<>();
		players.add(new Player("Alice", "AA"));
		players.add(new Player("Bob", "BB"));
		LeaderBoard lb = new LeaderBoard(players);
		Exchange exchange = new Exchange();

		for (Player player : players) {
			exchange.addPlayer(player);
		}
		lb.get();
		exchange.startRound(stocks);

		exchange.addOrder(new BuyOrder(stocks.get(0), players.get(0), 75, 900));
		exchange.printOrderBooks();

		exchange.addOrder(new SellOrder(stocks.get(0), players.get(1), 75, 900));
		exchange.printOrderBooks();


		exchange.startRound(stocks);
		for (Player px : players) {
			System.out.println(px.getName() + " ");
			px.getPortfolio().contents();
		}
		for (Player px : players) {
			System.out.println(px.getName() + " -- " + px.getBalance());
		}
		for (Stock stock : stocks) {
			System.out.println(stock.getSymbol() + " " + stock.getStockPrice());
		}
		lb.get();
	}

	public static void basicOrderMatching() {
		ArrayList<Stock> stocks = new ArrayList<>();
		stocks.add(new Stock("BP", "British Petroleum", 5000, 0.2f, 1000, 100));

		ArrayList<Player> players = new ArrayList<>();
		players.add(new Player("Alice", "AA"));
		players.add(new Player("Bob", "BB"));
		LeaderBoard lb = new LeaderBoard(players);
		Exchange exchange = new Exchange();

		for (Player player : players) {
			exchange.addPlayer(player);
		}
		lb.get();
	}

	public static void main(String[] args) {
		singleTradeExchange();
		//voidExchange();
		//basicOrderMatching();
	}
}
