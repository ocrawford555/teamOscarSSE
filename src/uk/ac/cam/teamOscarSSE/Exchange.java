package uk.ac.cam.teamOscarSSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The exchange takes and processes orders.
 */
public class Exchange {
	//TODO constant limits.
	private final long MAX_STOCK_PRICE = 100000;
	private final long MAX_STOCK_SHARES = 5000;

	private HashMap<String, OrderBook> orderBooks;
	private HashMap<String, Player> players;
	private HashMap<Long, Order> orders;

	private boolean open;    // default state is closed.
	private long startTime;
	private long lastRoundUptime;

	public Exchange(List<Stock> stocks) {
		orderBooks = new HashMap<>();
		players = new HashMap<>();
		orders = new HashMap<>();
		open = false;
		lastRoundUptime = 0;

		startTime = System.currentTimeMillis();
		String dateFormatted = getFormattedTime(startTime);

		String debugString = "";

		for (Stock stock : stocks) {
			orderBooks.put(stock.getSymbol(), new OrderBook(stock));
			debugString += stock.getSymbol() + " ";
		}
		System.out.println("Exchanged created at " + dateFormatted + "." + " --  Available stocks: " + debugString);
	}

	private synchronized String getFormattedTime(long millis) {
		Date date = new Date(millis);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
		return formatter.format(date);
	}

	public synchronized void setOpen(boolean open) {
		if (open && !this.open) {
			startTime = System.currentTimeMillis();
			System.out.println("Exchanged started at " + getFormattedTime(startTime) + ".");
		} else if (this.open && !open) {
			lastRoundUptime = System.currentTimeMillis() - startTime;
		}
		this.open = open;
	}

	public synchronized boolean isOpen() {
		return open;
	}


	/**
	 * An order is invalid if
	 * - the price is negative
	 * - the price is above the maximum price
	 * - the number of shares is <= 0
	 * - the number of shares is above the maximum
	 * - the stock does not exist
	 * <p>
	 * This method assumes other properties of Order are valid, but may do other checks as well.
	 *
	 * @param order
	 * @return
	 */
	private synchronized boolean validateOrder(Order order) {
		if (order.getPrice() <= 0) {
			return false;
		} else if (order.getPrice() > MAX_STOCK_PRICE) {
			return false;
		} else if (order.getShares() <= 0) {
			return false;
		} else if (order.getShares() > MAX_STOCK_SHARES) {
			return false;
		} else if (!orderBooks.containsKey(order.getStock().getSymbol())) {
			return false;
		} else if (!players.containsKey(order.getId())) {
			return false;
		}
		return true;
	}

	/**
	 * Adds an order to the exchange, trying to match immediately if possible.
	 * The caller should check if successful or not.
	 * Failure may be due to invalid orders (e.g. negative price), as well as players with insufficient funds/stocks.
	 *
	 * @param order
	 * @return true if successful, false otherwise.
	 */
	public synchronized boolean addOrder(Order order) {
		if (!isOpen()) {
			System.err.println("Can't add order. The exchange is closed.");
			return false;
		}
		// TODO: should return exception with a message, discuss.
		if (!validateOrder(order)) {
			System.err.println("Invalid order " + order);
			return false;
		}
		OrderBook ob = orderBooks.get(order.getStock().getSymbol());
		Player player = players.get(order.getId());

		if (order instanceof BuyOrder) {
			BuyOrder bo = (BuyOrder) order;
			if (bo.getShares() > player.maxCanBuy(order.getStock(), order.getPrice())) {
				//System.err.println("Player does not have enough cash to buy " + order);
				// TODO
				return false;
			}
			orders.put(order.getOrderNum(), order);
			player.addPendingOrder(bo);
			ob.addOrder(bo);
		} else if (order instanceof SellOrder) {
			SellOrder so = (SellOrder) order;
			if (so.getShares() > player.maxCanSell(order.getStock())) {
				//System.err.println("Player does not have enough shares to sell " + order);
				// TODO
				 return false;
			}
			orders.put(order.getOrderNum(), order);
			player.addPendingOrder(so);
			ob.addOrder(so);
		} else {
			System.err.println("Unimplemented order type: " + order.getClass());
			return false;
		}

		matchOrders(ob);
		return true;
	}

	/**
	 * Returns pending order corresponding to orderNum and null if the order does not exist.
	 * Note that a completed order may no longer be in the exchange.
	 *
	 * @param orderNum
	 * @return
	 */
	public synchronized Order getOrder(Long orderNum) {
		return orders.get(orderNum);
	}

	/**
	 * Get pending orders in order book.
	 * @param playerID
	 * @return
	 */
	public synchronized Map<Long, Order> getPendingOrders(String playerID) {
		Player player = players.get(playerID);
		if (player == null) {
			return null;
		}
		return player.getPendingOrders();
	}

	/**
	 * Cancel all players' orders, returning true upon success and false otherwise.
	 * @param playerID
	 * @return
	 */
	public synchronized boolean removeAllOrders(String playerID) {
		Map<Long, Order> pending_orders = getPendingOrders(playerID);
		boolean good = true;
		for (Map.Entry<Long, Order> entry : pending_orders.entrySet()) {
			if (!removeOrder(entry.getKey())) {
				good = false;
			}
		}
		return good;
	}

	/**
	 * Remove an order from the orderbook and player's portfolio.
	 *
	 * @param orderNum
	 * @return
	 */
	public synchronized boolean removeOrder(Long orderNum) {
		// Check if both orderbook and player contains ordernum.
		Order order = orders.get(orderNum);
		if (order == null) {
			System.err.println("Order does not exist.");
			return false;
		}
		Player player = players.get(order.getId());

		if (player != null && player.hasOrderPending(orderNum)) {
			player.removeOrder(orderNum);
			if (order instanceof BuyOrder) {
				return orderBooks.get(order.getStock().getSymbol()).removeOrder((BuyOrder) order);
			} else if (order instanceof SellOrder) {
				return orderBooks.get(order.getStock().getSymbol()).removeOrder((SellOrder) order);
			} else {
				System.err.println("Unimplemented type " + order.getClass());
				return false;
			}
		}
		return false;
	}

	/**
	 * This method does best effort matching on the order book.
	 * It assumes all orders can go through as cash and stock were already blocked when the user submitted the order.
	 * This allows users to trade with themselves, so a buy above their own sell will go through.
	 *
	 * @param ob
	 */
	private synchronized void matchOrders(OrderBook ob) {
		if (ob.buys.size() == 0 || ob.sells.size() == 0) {
			// Not possible to match.
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
			players.get(bo.getId()).updatePortfolio(buyUpdate);

			OrderUpdateMessage sellUpdate = new OrderUpdateMessage(so, sizeFilled, price);
			players.get(so.getId()).updatePortfolio(sellUpdate);
			bo.getStock().newPrice();

			if (bo.getShares() == 0) {
				// Order filled, remove from order book.
				ob.removeOrder(bo);
				orders.remove(bo.getOrderNum());
				if (ob.buys.size() > 0) {
					bo = ob.buys.get(0);
				}
			}

			if (so.getShares() == 0) {
				// Order filled, remove from order book.
				ob.removeOrder(so);
				orders.remove(so.getOrderNum());
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
	public synchronized boolean addPlayer(Player player) {
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

	public synchronized Player getPlayer(String userToken) {
		return players.get(userToken);
	}

	public synchronized Set<String> getStockSymbols() {
		return orderBooks.keySet();
	}

	public synchronized Stock getStockForSymbol(String symbol) {
		OrderBook orderBook = orderBooks.get(symbol);
		if (orderBook != null) {
			return orderBook.getStock();
		} else {
			return null;
		}
	}

	public synchronized Collection<Player> getPlayers() {
		return players.values();
	}

	/**
	 * Gets the number of milliseconds for which the exchange has been running
	 * If the exchange is closed, returns the last uptime.
	 */
	public synchronized long getUptime() {
		if (open) {
			return System.currentTimeMillis() - startTime;
		} else {
			return lastRoundUptime;
		}
	}

	public synchronized String getUptimeFormatted() {
		return getDurationBreakdown(getUptime());
	}

	/**
	 * Convert a millisecond duration to a string format
	 *
	 * @param millis A duration to convert to a string form
	 * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
	 */
	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		StringBuilder sb = new StringBuilder(64);
		sb.append(days);
		sb.append(" Days ");
		sb.append(hours);
		sb.append(" Hours ");
		sb.append(minutes);
		sb.append(" Minutes ");
		sb.append(seconds);
		sb.append(" Seconds");

		return (sb.toString());
	}

	public synchronized void printOrderBooks() {
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

	public synchronized OrderBook getOrderBook(String symbol) {
		return orderBooks.get(symbol);
	}
}