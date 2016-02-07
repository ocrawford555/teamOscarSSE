package uk.ac.cam.teamOscarSSE;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * The exchange takes and processes orders.
 */
public class Exchange {

	private static long GLOBAL_ORDER_ID = 0;

	private HashMap<String, OrderBook> orderBooks;
	private HashMap<String, Player> players;

	public Exchange(List<Stock> stocks) {
		orderBooks = new HashMap<String, OrderBook>();
		players = new HashMap<String, Player>();

		String debugString = "";

		for (Stock stock : stocks) {
			orderBooks.put(stock.getSymbol(), new OrderBook(stock));
			debugString += stock.getSymbol() + " ";
		}
		System.out.println("Exchanged started. Available stocks: " + debugString);
	}

	public boolean addOrder(Order order) {
		OrderBook ob = orderBooks.get(order.getStock().getSymbol());
		if (ob == null) {
			System.err.format(
					"Can not add order with symbol %s. Symbol does not exist.\n",
					order.getStock().getSymbol());
			Main.onOrderChange(new OrderChangeMessage(OrderChangeMessage.ChangeType.FAIL, order));
			return false;
		}

		if (order instanceof BuyOrder) {
			BuyOrder bo = (BuyOrder) order;
			ob.addOrder(bo);
		} else if (order instanceof SellOrder) {
			SellOrder so = (SellOrder) order;
			ob.addOrder(so);
		}
		Main.onOrderChange(new OrderChangeMessage(OrderChangeMessage.ChangeType.ACK, order));

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

			System.out.println("Matched orders: " + bo + " " + so);

			bo.setShares(bo.getShares() - sizeFilled);
			so.setShares(so.getShares() - sizeFilled);

			OrderUpdateMessage buyUpdate = new OrderUpdateMessage(bo, sizeFilled, price);
			Main.onOrderChange(buyUpdate);
			players.get(bo.getId()).updatePortfolio(buyUpdate);

			OrderUpdateMessage sellUpdate = new OrderUpdateMessage(so, sizeFilled, price);
			Main.onOrderChange(sellUpdate);
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
			ob.printPendingOrders(OrderType.BUY);
			ob.printPendingOrders(OrderType.SELL);
			System.out.println("=================");
		}
	}
}
