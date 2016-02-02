package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;

public class Main {
	// If we decide to use sockets, we can send users a change message here.
	public static void onOrderChange(OrderChangeMessage msg) {
		System.out.println("onOrderChange: " + msg);
	}

	public static void testExchange() {
		ArrayList<Stock> stocks = new ArrayList<Stock>();
		Stock stock1 = new Stock("BP", "British Petroleum", 5000);
		stocks.add(stock1);
		Stock stock2 = new Stock("BAM", "Bank of America", 12000);
		stocks.add(stock2);

		ArrayList<Player> players = new ArrayList<Player>();
		Player Oliver = new Player("Oliver", "o.crawford@hotmail.co.uk");
		players.add(Oliver);
		Player Alice = new Player("Alice", "art35@cam.ac.uk");
		players.add(Alice);
		Player Bob = new Player("Bob", "brt35@cam.ac.uk");
		players.add(Bob);
		LeaderBoard lb = new LeaderBoard(players);
		lb.get();
		System.out.println();

		Exchange exchange = new Exchange(stocks);
		for (Player player : players) {
			exchange.addPlayer(player);
		}

		exchange.addOrder(new BuyOrder(stock1, Oliver, 100, 54.34));
		exchange.addOrder(new BuyOrder(stock1, Alice, 150, 57.01));
		exchange.addOrder(new BuyOrder(stock1, Alice, 100, 54.34));
		exchange.addOrder(new BuyOrder(stock1, Oliver, 270, 57.01));
		exchange.addOrder(new BuyOrder(stock2, Alice, 100, 54.34));
		exchange.addOrder(new BuyOrder(stock2, Alice, 150, 57.01));
		exchange.addOrder(new BuyOrder(stock2, Oliver, 100, 54.34));
		exchange.addOrder(new BuyOrder(stock2, Oliver, 270, 57.01));

		exchange.printOrderBooks();

		exchange.addOrder(new SellOrder(stock1, Oliver, 230, 59.56));
		exchange.addOrder(new SellOrder(stock1, Oliver, 122, 56.56));
		exchange.addOrder(new SellOrder(stock1, Oliver, 230, 58.32));
		exchange.addOrder(new SellOrder(stock1, Oliver, 1230, 56.59));

		exchange.printOrderBooks();
	}

	public static void main(String args[]) {
		//test();
		testExchange();

	}
}