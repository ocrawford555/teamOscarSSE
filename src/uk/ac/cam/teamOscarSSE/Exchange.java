package uk.ac.cam.teamOscarSSE;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * The exchange takes and processes orders.
 */
public class Exchange {

	private HashMap<String, OrderBook> orderBooks;
	private HashMap<String, Player> players;

	private boolean open;	// default state is closed.

	public Exchange(List<Stock> stocks) {
		orderBooks = new HashMap<>();
		players = new HashMap<>();
		open = false;

		String debugString = "";

		for (Stock stock : stocks) {
			orderBooks.put(stock.getSymbol(), new OrderBook(stock));
			debugString += stock.getSymbol() + " ";
		}
		System.out.println("Exchanged started. Available stocks: " + debugString);
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isOpen() {
		return open;
	}

	/*
	//Variables that will be used to provide users with metrics
	private static long pointAvg5;
	private static long pointAvg20;
	private static long pointAvg50;
	private static long overallAverage;
	private static long transactionChange;
	private static float rateOfChange5;
	private static float rateOfChange20;
	private static float rateOfChange50;

	//Getter methods for user metrics
	public static long getPointAvg6() {
		return pointAvg5;
	}

	public static long getPointAvg20() {
		return pointAvg20;
	}

	public static long getPointAvg50() {
		return pointAvg50;
	}

	public static long overallAverage() {
		return overallAverage;
	}

	public static long transactionChange() {
		return transactionChange;
	}

	public static float rateOfChange5() {
		return rateOfChange5;
	}

	public static float rateOfChange20() {
		return rateOfChange20;
	}

	public static float rateOfChange50() {
		return rateOfChange50;
	}
	//End of getter methods
	*/

	public boolean addOrder(Order order) {
		OrderBook ob = orderBooks.get(order.getStock().getSymbol());
		if (ob == null) {
			System.err.format(
					"Can not add order with symbol %s. Symbol does not exist.\n",
					order.getStock().getSymbol());
			//Main.onOrderChange(new OrderChangeMessage(OrderChangeMessage.ChangeType.FAIL, order));
			return false;
		}

		if (order instanceof BuyOrder) {
			BuyOrder bo = (BuyOrder) order;
			ob.addOrder(bo);
		} else if (order instanceof SellOrder) {
			SellOrder so = (SellOrder) order;
			ob.addOrder(so);
		}
		//Main.onOrderChange(new OrderChangeMessage(OrderChangeMessage.ChangeType.ACK, order));

		matchOrders(ob);
		return true;
	}

	private void matchOrders(OrderBook ob) {
		if (ob.buys.size() == 0 || ob.sells.size() == 0) {
			return;
		}

		BuyOrder bo = ob.buys.get(0);
		SellOrder so = ob.sells.get(0);
		while (ob.buys.size() > 0 && ob.sells.size() > 0 && bo.getPrice() >= so.getPrice()) {
			// Match order
			int sizeFilled = Math.min(bo.getShares(), so.getShares());
			long price = bo.getTime() < so.getTime() ? bo.getPrice() : so.getPrice();

			//System.out.println("Matched orders: " + bo + " " + so);

			bo.getStock().addVolume(sizeFilled);
			bo.getStock().setLastTransactionPrice(price);
			
			bo.setShares(bo.getShares() - sizeFilled);
			so.setShares(so.getShares() - sizeFilled);

			OrderUpdateMessage buyUpdate = new OrderUpdateMessage(bo, sizeFilled, price);
			//Main.onOrderChange(buyUpdate);
			players.get(bo.getId()).updatePortfolio(buyUpdate);

			OrderUpdateMessage sellUpdate = new OrderUpdateMessage(so, sizeFilled, price);
			//Main.onOrderChange(sellUpdate);
			players.get(so.getId()).updatePortfolio(sellUpdate);

			if (bo.getShares() == 0) {
				// Order filled, remove from order book.
				ob.removeOrder(bo);
				if (ob.buys.size() > 0) {
					bo = ob.buys.get(0);
				}
			}

			if (so.getShares() == 0) {
				// Order filled, remove from order book.
				ob.removeOrder(so);
				if (ob.sells.size() > 0) {
					so = ob.sells.get(0);
				}
			}
		}
	}

	/**
	 * Adds a player to the exchange.
	 *
	 * @param player
	 * @return True if successful, false if player already exists in the exchange.
	 */
	public boolean addPlayer(Player player) {
		if (player == null) {
			System.err.println("player should not be null.");
			return false;
		}
		if (players.containsKey(player.getToken())) {
			System.err.format("Player %s already exists in the exchange.\n", player.getName());
			return false;
		}
		players.put(player.getToken(), player);
		System.out.format("Welcome %s!\n", player.getName());
		return true;
	}
	
	public Set<String> getStockSymbols() {
		return orderBooks.keySet();
	}
	
	public Stock getStockForSymbol(String symbol) {
		OrderBook orderBook = orderBooks.get(symbol);
		if (orderBook != null) {
			return orderBook.getStock();
		} else {
			return null;
		}
	}
	
	public Collection<Player> getPlayers() {
		return players.values();
	}
	
	/**
	 * Gets the number of milliseconds for which the exchange has been running
	 */
	public int getUptime() {
		//TODO
		return 0;
	}

	public void printOrderBooks() {
		for (OrderBook ob : orderBooks.values()) {
			System.out.println("BUYS TOP FIVE");
			ob.printPendingOrders(OrderType.BUY);
			System.out.println("");
			System.out.println("");
			System.out.println("SELLS TOP FIVE");
			ob.printPendingOrders(OrderType.SELL);
			System.out.println("=================");
			System.out.println("");
		}
	}
}