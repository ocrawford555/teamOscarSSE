package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;

public class TestMain {
	public static final int BUY = 1;
	public static final int SELL = 2;

	public static void main(String args[]) {
		ArrayList<Stock> stocks = new ArrayList<Stock>();
		Stock stock1 = new Stock("BP", "British Petroleum", 5000, 0.2F);
		stocks.add(stock1);
		Stock stock2 = new Stock("BAM", "Bank of America", 12000, 0.2F);
		stocks.add(stock2);
		StockManager allStocks = null;
		allStocks = StockManager.getInstance(stocks);
		allStocks.printStocks();

		ArrayList<Player> players = new ArrayList<Player>();
		Player Oliver = new Player("Oliver", "o.crawford@hotmail.co.uk");
		players.add(Oliver);
		Player Alice = new Player("Alice", "art35@cam.ac.uk");
		players.add(Alice);
		Player Bob = new Player("Bob", "brt35@cam.ac.uk");
		players.add(Bob);
		LeaderBoard lb = new LeaderBoard(players);
		lb.get();

		//want to change this style slightly.
		//BuyOrder doesn't need stock reference.
		//Leaving it for the moment, but make
		//sure stock passed to BuyOrder IS THE
		//SAME as the stock linked to the ORDER
		//BOOK.
		OrderBook ob_BP = new OrderBook(stock1);
		OrderBook ob_BAM = new OrderBook(stock2);
		ob_BP.addOrder(new BuyOrder(stock1, Oliver, 100, 5434));
		ob_BP.addOrder(new BuyOrder(stock1, Alice, 150, 5701));
		ob_BP.addOrder(new BuyOrder(stock1, Alice, 100, 5434));
		ob_BP.addOrder(new BuyOrder(stock1, Oliver, 270, 5701));
		ob_BAM.addOrder(new BuyOrder(stock2, Alice, 100, 5434));
		ob_BAM.addOrder(new BuyOrder(stock2, Alice, 150, 5701));
		ob_BAM.addOrder(new BuyOrder(stock2, Oliver, 100, 5434));
		ob_BAM.addOrder(new BuyOrder(stock2, Oliver, 270, 5701));
		//ob_BP.printPendingOrders(BUY);

		//System.out.println("");

		ob_BP.addOrder(new SellOrder(stock1, Oliver, 230, 5956));
		ob_BP.addOrder(new SellOrder(stock1, Oliver, 122, 5656));
		ob_BP.addOrder(new SellOrder(stock1, Oliver, 230, 5832));
		ob_BP.addOrder(new SellOrder(stock1, Oliver, 1230, 5659));
		//ob_BP.printPendingOrders(SELL);

	}
}
