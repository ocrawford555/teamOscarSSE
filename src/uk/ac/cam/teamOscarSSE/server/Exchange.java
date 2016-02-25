package uk.ac.cam.teamOscarSSE.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The exchange takes and processes orders.
 */
public class Exchange {
	//DEBUG variable for outputting information to the console.
	private final boolean DEBUG = false;

	// DEBUG verbosity. A higher verbosity means more is printed.
	private final int DEBUG_LEVEL = 2;

	// The maximum price at which a user can buy/sell a stock.
	private final long MAX_STOCK_PRICE = 1000000;

	// The maximum number of shares a user can buy/sell.
	private final long MAX_STOCK_SHARES = 10000;

	// A map from stock symbol to OrderBook for each stock.
	private Map<String, OrderBook> orderBooks;

	// A map from trader ID to Trader participating in the exchange.
	private Map<String, Trader> traders;

	// A list of players who have (or attempted to) issue an order in the current/last round.
	private Map<String, Player> activePlayers;

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

	// The round's end time: set when the exchange changes state from closed to open.
	private long endTime;

	// The round's uptime. This is frozen when the exchange is closed until it is reopen.
	private long lastRoundUptime;

	public Exchange() {
		orderBooks = new HashMap<>();
		traders = new HashMap<>();
		orders = new HashMap<>();
		players = new HashMap<>();
		activePlayers = new HashMap<>();
		open = false;
		lastRoundUptime = 0;

		startTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
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
	 * @return true if the exchange is open
	 */
	public synchronized boolean isOpen() {
		return open;
	}

	/**
	 * Returns the timestamp of the last time a round was started.
	 *
	 * @return the start time of the round
	 */
	public synchronized long getRoundStart() {
		return startTime;
	}
	
	/**
	 * Starts a round with the input stocks.
	 * <p>
	 * The round has indefinite length and will continue until endRound is called.
	 *
	 * @param stocks the stocks offered by the exchange
	 * @return true if a round was successfully started
	 */
	public synchronized boolean startRound(List<Stock> stocks) {
		return startRound(stocks, -1, -1);
	}
	
	/**
	 * Starts a round with the input stocks and sets a timer to stop the round after
	 * roundLength seconds.
	 *
	 * This method removes all inactive players from the previous round from the
	 * exchange.
	 *
	 * @param stocks the stocks offered by the exchange
	 * @param roundLength the length of the round
	 * @return true if a round was successfully started
	 */
	public synchronized boolean startRound(List<Stock> stocks, int roundLength) {
		return startRound(stocks, roundLength, -1);
	}

	/**
	 * Starts a round with the input stocks and sets a timer to stop the round after
	 * roundLength seconds. After the round is over a count-down will be set indicating the time the next round will start.
	 *
	 * @param stocks the stocks offered by the exchange
	 * @param roundLength the length of the round
	 * @param timeBetweenRounds
	 * @return true if a round was successfully started
	 */
	public synchronized boolean startRound(List<Stock> stocks, int roundLength, final int timeBetweenRounds) {
		if (open) {
			System.err.println("Failed to start round: a round is already in progress.");
			return false;
		}

		if (stocks == null || stocks.size() == 0) {
			System.err.println("Failed to start round: tried to start a round with no stocks.");
			return false;
		}
		// Clear orders and orderbooks.
		orderBooks.clear();
		orders.clear();
		traders.clear();
		players.clear();

		// Only add activePlayers from the previous round to the new round.
		for (Map.Entry<String, Player> entry : activePlayers.entrySet()) {
			traders.put(entry.getKey(), entry.getValue());
			players.put(entry.getKey(), entry.getValue());
			entry.getValue().reset();
		}

		activePlayers.clear();

		// Add new orderbooks.
		String stockString = "";
		for (Stock stock : stocks) {
			orderBooks.put(stock.getSymbol(), new OrderBook(stock));
			stockString += stock.getSymbol() + " ";
		}

		startTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();

		// Set timer to stop the round if roundLength is greater than 0.
		if (roundLength > 0) {
			endTime = startTime + 1000 * roundLength;

			Timer endTimer = new Timer();
			Date endDate = new Date();
			endDate.setTime(endDate.getTime() + 1000 * roundLength);
			endTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					endRound();
					if (timeBetweenRounds >= 0) {
						endTime = System.currentTimeMillis() + 1000 * timeBetweenRounds;
					}
				}
			}, endDate);
		}

		// Set the status of the exchange to open.
		open = true;

		System.out.format(
				"Exchange started at %s.\n" +
						"Available stocks: %s\n" +
						"Round length: %s seconds\n",
				getFormattedTime(startTime), stockString, roundLength > 0 ? roundLength : "unknown");
		return true;
	}

	/**
	 * Ends a round in the exchange.
	 * If the exchange already has a timer set to automatically end the round, returns false.
	 *
	 * @return true if the round was successfully ended
	 */
	public synchronized boolean endRound() {
		if (!open) {
			System.err.println("Failed to end round: no round is in progress.");
			return false;
		} else if (endTime > System.currentTimeMillis()) {
			System.err.println("Failed to end round: round will automatically end.");
			return false;
		}
		lastRoundUptime = System.currentTimeMillis() - startTime;
		System.out.println("Round ended.");
		open = false;
		return true;
	}

	/**
	 * An order is invalid if
	 * - the price is negative
	 * - the price is above the maximum price
	 * - the number of shares is <= 0
	 * - the number of shares is above the maximum
	 * - the stock does not exist
	 *
	 * This method assumes other properties of Order are valid, but may do other checks as well.
	 *
	 * @param order the order to be checked
	 * @return true if the order is valid
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
	 * @param order the order to add to the exchange
	 * @return OrderChangeMessage with type set to FAIL if the order did not go
	 * through, and set to ACK if the order was successfully added to the exechange.
	 */
	public synchronized OrderChangeMessage addOrder(Order order) {
		if (!isOpen()) {
			debugPrint("Can't add order. The exchange is closed.", 3);
			return new OrderChangeMessage(
					OrderChangeMessage.ChangeType.FAIL,
					order, "The exchange is closed.");
		}
		if (!validateOrder(order)) {
			debugErrPrint("Invalid order " + " - " + traders.get(order.getId()).getName() +
					" - " + order, 2);
			return new OrderChangeMessage(
					OrderChangeMessage.ChangeType.FAIL,
					order, "The order was invalid.");
		}
		OrderBook ob = orderBooks.get(order.getStock().getSymbol());
		Trader trader = traders.get(order.getId());

		if (trader instanceof Player && !activePlayers.containsKey(order.getId())) {
			activePlayers.put(trader.getToken(), (Player) trader);
		}

		OrderChangeMessage ret = null;

		if (order instanceof BuyOrder) {
			BuyOrder bo = (BuyOrder) order;
			if (bo.getShares() > trader.maxCanBuy(order.getStock(), order.getPrice())) {
				debugErrPrint(String.format(
						"%s does not have enough cash to buy %s",
						trader.getName(), order));
				return new OrderChangeMessage(
						OrderChangeMessage.ChangeType.FAIL,
						order, "Not enough cash to buy.");
			}
			orders.put(order.getOrderNum(), order);
			trader.addPendingOrder(bo);
			ob.addOrder(bo);
			debugPrint("Added order: " + order, 5);
			ret = new OrderChangeMessage(
					OrderChangeMessage.ChangeType.ACK, order,
					String.format("BUY %d %s at %d", order.getShares(),
							order.getStock().getSymbol(), order.getPrice()));
		} else if (order instanceof SellOrder) {
			SellOrder so = (SellOrder) order;
			if (so.getShares() > trader.maxCanSell(order.getStock())) {
				debugErrPrint(String.format(
						"%s does not have enough shares to sell %s",
						trader.getName(), order));
				return new OrderChangeMessage(
						OrderChangeMessage.ChangeType.FAIL,
						order, "Not enough shares to sell.");
			}
			orders.put(order.getOrderNum(), order);
			trader.addPendingOrder(so);
			ob.addOrder(so);
			debugPrint("Added order : " + order, 5);
			ret = new OrderChangeMessage(
					OrderChangeMessage.ChangeType.ACK, order,
					String.format("SELL %d %s at %d", order.getShares(),
							order.getStock().getSymbol(), order.getPrice()));
		} else {
			System.err.println("Unimplemented order type: " + order.getClass());
			return new OrderChangeMessage(
					OrderChangeMessage.ChangeType.FAIL,
					order, "The order was invalid.");
		}
		matchOrders(ob);
		return ret;
	}

	/**
	 * Returns pending order corresponding to orderNum and null if the order
	 * does not exist.
	 * <p>
	 * Note that a completed order may no longer be in the exchange.
	 *
	 * @param orderNum the unique order number
	 * @return the order corresponding to orderNum
	 */
	public synchronized Order getOrder(Long orderNum) {
		return orders.get(orderNum);
	}

	/**
	 * Get a trader's orders that are pending in the exchange.
	 *
	 * @param traderID the unique token corresponding to the trader to lookup
	 * @return a map from orderNum to order of the trader's pending orders
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
	 * @param traderID the unique if of the trader
	 * @return true if all orders were successfully removed
	 */
	public synchronized boolean removeAllOrders(String traderID) {
		boolean good = true;

		Set<Long> orders2 = new TreeSet<>(getPendingOrders(traderID).keySet());

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
	 * @param orderNum the order number of the order to be removed
	 * @return true if the order was successfully removed
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
	 * @param ob the orderbook in which to match orders
	 */
	private synchronized void matchOrders(OrderBook ob) {
		if (ob.getBuys().size() == 0 || ob.getSells().size() == 0) {
			// Not possible to match.
			return;
		}

		BuyOrder bo = ob.getBuys().get(0);
		SellOrder so = ob.getSells().get(0);
		while (ob.getBuys().size() > 0 && ob.getSells().size() > 0 && bo.getPrice() >= so.getPrice()) {
			// Match order
			int sizeFilled = Math.min(bo.getShares(), so.getShares());
			long price = bo.getTime() < so.getTime() ? bo.getPrice() : so.getPrice();

			debugPrint("Matched orders: " + bo + " " + so, 5);

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
				if (ob.getBuys().size() > 0) {
					bo = ob.getBuys().get(0);
				}
			}

			if (so.getShares() == 0) {
				// Order filled, remove from order book.
				ob.removeOrder(so);
				orders.remove(so.getOrderNum());
				if (ob.getSells().size() > 0) {
					so = ob.getSells().get(0);
				}
			}
		}
	}

	/**
	 * Adds a trader to the exchange.
	 *
	 * @param trader the trader to be added to the exchange
	 * @return True if successful, false if trader already exists in the exchange.
	 */
	public synchronized boolean addPlayer(Trader trader) {
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
			activePlayers.put(trader.getToken(), (Player) trader);
		}
		System.out.format("Welcome %s!\n", trader.getName());
		return true;
	}

	/**
	 * Returns the player with the given userToken.
	 * <p>
	 * If the userToken does not exist or corresponds to a bot, null is returned.
	 *
	 * @param userToken the unique user token
	 * @return the player corresponding to the token, or null if it does not exist
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
	 * @return the set of symbols of stocks available on the exchange
	 */
	public synchronized Set<String> getStockSymbols() {
		return orderBooks.keySet();
	}

	/**
	 * Returns the stock corresponding to the stock symbol.
	 *
	 * @param symbol the symbol of the stock to look up, e.g. "BAML"
	 * @return the stock corresponding to the stock symbol
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
	 * Returns a collection of "human" players that issued a valid order in the current/last round.
	 *
	 * @return a collection of "human" players that issued a valid order in the exchange
	 */
	public synchronized Collection<Player> getPlayers() {
		return activePlayers.values();
	}

	/**
	 * Returns a collection of all traders on the exchange.
	 * This includes "human" players as well as bots.
	 *
	 * @return a collection of all traders on the exchange
	 */
	public synchronized Collection<Trader> getTraders() {
		return traders.values();
	}

	/**
	 * Gets the number of milliseconds for which the exchange has been running
	 * If the exchange is closed, returns the last uptime.
	 *
	 * @return the number of milliseconds for which the exchange has been running
	 */
	public synchronized long getUptime() {
		if (open) {
			return System.currentTimeMillis() - startTime;
		} else {
			return lastRoundUptime;
		}
	}

	/**
	 * Gets the remaining number of milliseconds for which the exchange will be running
	 * If the exchange is closed, returns the 0.
	 *
	 * @return the remaining number of seconds for which the exchange will be running
	 */
	public synchronized long getRemainingTime() {
		if (open) {
			return Math.max(0, endTime - System.currentTimeMillis());
		} else {
			return Math.min(0, System.currentTimeMillis() - endTime);
		}
	}

	/**
	 * Returns the formatted time the exchange has been running.
	 * If the exchange is closed, returns the last uptime.
	 *
	 * @return the formmated time the exchange has been running
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

	/**
	 * Outputs the order book, up to maxNum buys and maxNum sells.
	 *
	 * @param maxNum the maximum number of buys or sells to output
	 */
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
	 * @param symbol the stock symbol, e.g. "BAML"
	 * @return the order book corresponding to the stock symbol
	 */
	public synchronized OrderBook getOrderBook(String symbol) {
		return orderBooks.get(symbol);
	}

	/**
	 * Prints to standard output if DEBUG is true at verbosity 1.
	 * @param s the string to be printed
	 */
	private void debugPrint(String s) {
		debugPrint(s, 1);
	}

	/**
	 * Prints s to standard output if DEBUG is true and DEBUG_LEVEL >= level
	 * @param s the string to print
	 * @param level the level of the string, a lower level is printed more
	 */
	private void debugPrint(String s, int level) {
		if (DEBUG && DEBUG_LEVEL >= level) {
			System.out.println(s);
		}
	}

	/**
	 * Prints to standard error if DEBUG is true at verbosity 1.
	 * @param s the string to be printed
	 */
	private void debugErrPrint(String s) {
		debugErrPrint(s, 1);
	}

	/**
	 * Prints s to standard error if DEBUG is true and DEBUG_LEVEL >= level
	 * @param s the string to print
	 * @param level the level of the string, a lower level is printed more
	 */
	private void debugErrPrint(String s, int level) {
		if (DEBUG && DEBUG_LEVEL >= level) {
			System.err.println(s);
		}
	}
}