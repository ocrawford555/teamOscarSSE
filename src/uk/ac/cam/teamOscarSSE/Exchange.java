package uk.ac.cam.teamOscarSSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The exchange takes and processes orders.
 */
public class Exchange {
	private final boolean DEBUG = true;

	// The maximum price at which a user can buy/sell a stock.
	private final long MAX_STOCK_PRICE = 1000000;

	// The maximum number of shares a user can buy/sell.
	private final long MAX_STOCK_SHARES = 10000;

	public List<Long> prices = new LinkedList<Long>();

	// A map from stock symbol to OrderBook for each stock.
	private Map<String, OrderBook> orderBooks;

	// A map from trader ID to Trader participating in the exchange.
	private Map<String, Trader> traders;

	// A map from player ID to Player participating in the exchange.
	// This is a subset of traders and is used to provide a method
	// to access "human" players.
	private Map<String, Player> players;

	// A map from orderNum to Order containing pending orders in the exchange.
	private Map<Long, Order> orders;

	// Represents whether the exchange is open or closed. The default state is closed.
	private boolean open;

	// The round's start time: set when the exchange changes state from closed to open.
	private long startTime;

	// The round's uptime. This is frozen when the exchange is closed until it is reopen.
	private long lastRoundUptime;

	public Exchange(List<Stock> stocks) {
		if (stocks == null) {
			System.err.println("Tried to create an exchange with no stocks.");
			stocks = new ArrayList<>();
		}
		orderBooks = new HashMap<>();
		traders = new HashMap<>();
		orders = new HashMap<>();
		players = new HashMap<>();
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

	/**
	 * Formats the time given im millis in HH:mm:ss:SSS format.
	 *
	 * @param millis
	 * @return
	 */
	private synchronized String getFormattedTime(long millis) {
		Date date = new Date(millis);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
		return formatter.format(date);
	}

	/**
	 * Returns whether or not the exchange is open and able to accept orders.
	 *
	 * @return
	 */
	public synchronized boolean isOpen() {
		return open;
	}

	/**
	 * Changes the open/closed state of the exchange.
	 *
	 * @param open
	 */
	public synchronized void setOpen(boolean open) {
		if (open && !this.open) {
			startTime = System.currentTimeMillis();
			System.out.println("Exchanged started at " + getFormattedTime(startTime) + ".");
		} else if (this.open && !open) {
			lastRoundUptime = System.currentTimeMillis() - startTime;
		}
		this.open = open;
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
		} else if (!traders.containsKey(order.getId())) {
			return false;
		}
		return true;
	}

	/**
	 * Adds an order to the exchange, trying to match it immediately if possible.
	 * The caller should check if this was successful or not.
	 * Failure may be due to invalid orders (e.g. negative price), as well as
	 * traders with insufficient funds/stocks.
	 *
	 * @param order
	 * @return true if successful, false otherwise.
	 */
	public synchronized boolean addOrder(Order order) {
		if (!isOpen()) {
			if (DEBUG) {
				System.err.println("Can't add order. The exchange is closed.");
			}
			return false;
		}
		// TODO: should return exception with a message, discuss.
		if (!validateOrder(order)) {
			System.err.println("Invalid order " + " - " + traders.get(order.getId()).getName() +
					" - "  + order);
			return false;
		}
		OrderBook ob = orderBooks.get(order.getStock().getSymbol());
		Trader trader = traders.get(order.getId());

		if (order instanceof BuyOrder) {
			BuyOrder bo = (BuyOrder) order;
			if (bo.getShares() > trader.maxCanBuy(order.getStock(), order.getPrice())) {
				if (DEBUG) {
					System.err.format("%s does not have enough cash to buy %s\n", trader.getName(), order);
				}
				return false;
			}
			orders.put(order.getOrderNum(), order);
			trader.addPendingOrder(bo);
			ob.addOrder(bo);
		} else if (order instanceof SellOrder) {
			SellOrder so = (SellOrder) order;
			if (so.getShares() > trader.maxCanSell(order.getStock())) {
				if (DEBUG) {
					System.err.format("%s does not have enough shares to sell %s\n", trader.getName(), order);
				}
				return false;
			}
			orders.put(order.getOrderNum(), order);
			trader.addPendingOrder(so);
			ob.addOrder(so);
		} else {
			System.err.println("Unimplemented order type: " + order.getClass());
			return false;
		}

		matchOrders(ob);
		return true;
	}

	/**
	 * Returns pending order corresponding to orderNum and null if the order
	 * does not exist.
	 * <p>
	 * Note that a completed order may no longer be in the exchange.
	 *
	 * @param orderNum
	 * @return
	 */
	public synchronized Order getOrder(Long orderNum) {
		return orders.get(orderNum);
	}

	/**
	 * Get a trader's orders that are pending in the exchange.
	 *
	 * @param traderID
	 * @return
	 */
	public synchronized Map<Long, Order> getPendingOrders(String traderID) {
		Trader trader = traders.get(traderID);
		if (trader == null) {
			return null;
		}
		return trader.getPendingOrders();
	}

	/**
	 * Cancel all traders' orders, returning true upon success and false otherwise.
	 *
	 * @param traderID
	 * @return
	 */
	public synchronized boolean removeAllOrders(String traderID) {
		boolean good = true;
		Set<Long> orders = getPendingOrders(traderID).keySet();

		// TODO: temporary
		Set<Long> orders2 = new TreeSet(orders);

		for (Long orderNum : orders2) {
			if (!removeOrder(orderNum)) {
				good = false;
			}
		}

		return good;
	}

	/**
	 * Remove an order from the orderbook and trader's portfolio.
	 *
	 * @param orderNum
	 * @return
	 */
	public synchronized boolean removeOrder(Long orderNum) {
		// Check if both orderbook and trader contains ordernum.
		Order order = orders.get(orderNum);
		if (order == null) {
			// System.err.println("Order does not exist.");
			return false;
		}
		Trader trader = traders.get(order.getId());

		if (trader != null && trader.hasOrderPending(orderNum)) {
			trader.removeOrder(orderNum);
			orders.remove(orderNum);
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
			traders.get(bo.getId()).updatePortfolio(buyUpdate);

			OrderUpdateMessage sellUpdate = new OrderUpdateMessage(so, sizeFilled, price);
			traders.get(so.getId()).updatePortfolio(sellUpdate);
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

			//update line chart
			prices.add(ob.getStock().getPointAvg20());
			//prices.add(price);
		}
	}

	/**
	 * Adds a trader to the exchange.
	 *
	 * @param trader
	 * @return True if successful, false if trader already exists in the exchange.
	 */
	public synchronized boolean addPlayer(Trader trader) {
		// TODO: rename to addTrader
		if (trader == null) {
			System.err.println("trader should not be null.");
			return false;
		}
		if (traders.containsKey(trader.getToken())) {
			System.err.format("Player %s already exists in the exchange.\n", trader.getName());
			return false;
		}
		traders.put(trader.getToken(), trader);
		if (trader instanceof Player) {
			players.put(trader.getToken(), (Player) trader);
		}
		System.out.format("Welcome %s!\n", trader.getName());
		return true;
	}

	/**
	 * Returns the player with the given userToken.
	 * <p>
	 * If the userToken does not exist or corresponds to a bot, null is returned.
	 *
	 * @param userToken
	 * @return
	 */
	public synchronized Player getPlayer(String userToken) {
		Trader trader = traders.get(userToken);
		if (trader == null || !(trader instanceof Player)) {
			return null;
		} else {
			return (Player) trader;
		}
	}

	/**
	 * Returns the set of symbols of the stocks available on the exchange.
	 *
	 * @return
	 */
	public synchronized Set<String> getStockSymbols() {
		return orderBooks.keySet();
	}

	/**
	 * Returns the stock corresponding to the stock symbol.
	 *
	 * @param symbol
	 * @return
	 */
	public synchronized Stock getStockForSymbol(String symbol) {
		OrderBook orderBook = orderBooks.get(symbol);
		if (orderBook != null) {
			return orderBook.getStock();
		} else {
			return null;
		}
	}

	/**
	 * Returns a collection of "human" players on the exchange.
	 *
	 * @return
	 */
	public synchronized Collection<Player> getPlayers() {
		return players.values();
	}

	/**
	 * Returns a collection of all traders on the exchange.
	 * This includes "human" players as well as bots.
	 *
	 * @return
	 */
	public synchronized Collection<Trader> getTraders() {
		return traders.values();
	}

	/**
	 * Gets the number of milliseconds for which the exchange has been running
	 * If the exchange is closed, returns the last uptime.
	 *
	 * @return
	 */
	public synchronized long getUptime() {
		if (open) {
			return System.currentTimeMillis() - startTime;
		} else {
			return lastRoundUptime;
		}
	}

	/**
	 * Returns the formatted time the exchange has been running.
	 * If the exchange is closed, returns the last uptime.
	 *
	 * @return
	 */
	public synchronized String getUptimeFormatted() {
		return getDurationBreakdown(getUptime());
	}

	/**
	 * Outputs the order books. Defaults to print 5 buys and 5 sells.
	 */
	public synchronized void printOrderBooks() {
		printOrderBooks(5);
	}

	public synchronized void printOrderBooks(int maxNum) {
		for (OrderBook ob : orderBooks.values()) {
			System.out.println("BUYS TOP " + maxNum);
			ob.printPendingOrders(OrderType.BUY, maxNum);
			System.out.println("");
			System.out.println("");
			System.out.println("SELLS TOP " + maxNum);
			ob.printPendingOrders(OrderType.SELL, maxNum);
			System.out.println("=================");
			System.out.println("");
		}
	}

	/**
	 * Returns the order book corresponding to the stock symbol.
	 *
	 * @param symbol
	 * @return
	 */
	public synchronized OrderBook getOrderBook(String symbol) {
		return orderBooks.get(symbol);
	}
}