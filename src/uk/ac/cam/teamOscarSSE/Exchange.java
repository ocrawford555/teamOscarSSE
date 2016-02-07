package uk.ac.cam.teamOscarSSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The exchange takes and processes orders.
 */
public class Exchange {
	//TODO constant limits.
	private final long MAX_STOCK_PRICE = 1000000;
	private final long MAX_STOCK_SHARES = 10000;
	
	private long time;

	public long getStartTime() {
		return time;
	}

	private HashMap<String, OrderBook> orderBooks;
	private HashMap<String, Player> players;

	private boolean open;	// default state is closed.

	public Exchange(List<Stock> stocks) {
		orderBooks = new HashMap<>();
		players = new HashMap<>();
		open = false;

		time = System.currentTimeMillis();
		Date date = new Date(time);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
		String dateFormatted = formatter.format(date);
		
		String debugString = "";

		for (Stock stock : stocks) {
			orderBooks.put(stock.getSymbol(), new OrderBook(stock));
			debugString += stock.getSymbol() + " ";
		}
		System.out.println("Exchanged started at " + dateFormatted + "." + " --  Available stocks: " + debugString);
	}

	public synchronized void setOpen(boolean open) {
		this.open = open;
	}

	public synchronized boolean isOpen() {
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

	/**
	 * An order is invalid if
	 * - the price is negative
	 * - the price is above the maximum price
	 * - the number of shares is negative
	 * - the number of shares is above the maximum
	 * - the stock does not exist
	 *
	 * This method assumes other properties of Order are valid
	 * @param order
	 * @return
	 */
	private synchronized boolean validateOrder(Order order) {
		if (order.getPrice() < 0) {
			return false;
		} else if (order.getPrice() > MAX_STOCK_PRICE) {
			return false;
		} else if (order.getShares() < 0) {
			return false;
		} else if (order.getShares() > MAX_STOCK_SHARES) {
			return false;
		} else if (!orderBooks.containsKey(order.getStock().getSymbol())) {
			return false;
		}
		return true;
	}

	/**
	 * Adds an order to the exchange, trying to match immediately if possible.
	 * The caller should check if successful or not.
	 * Failure may be due to invalid orders (e.g. negative price), as well as players with insufficient funds/stocks.
	 * @param order
	 * @return true if successful, false otherwise.
	 */
	public synchronized boolean addOrder(Order order) {
		// TODO: should return exception, discuss.
		if (!validateOrder(order)) {
			System.err.println("Invalid order " + order);
			return false;
		}
		OrderBook ob = orderBooks.get(order.getStock().getSymbol());

		if (order instanceof BuyOrder) {
			BuyOrder bo = (BuyOrder) order;
			ob.addOrder(bo);
		} else if (order instanceof SellOrder) {
			SellOrder so = (SellOrder) order;
			ob.addOrder(so);
		} else {
			System.err.println("Unimplemented order type: " + order.getClass());
			return false;
		}

		matchOrders(ob);
		return true;
	}

	private synchronized void matchOrders(OrderBook ob) {
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
	 */
	public synchronized int getUptime() {
		int diff = (int) (System.currentTimeMillis() - getStartTime());
		return diff;
	}
	
	public synchronized String getUptimeFormatted() {
		long diff = (System.currentTimeMillis() - getStartTime());
		return getDurationBreakdown(diff);
	}
	
	/**
     * Convert a millisecond duration to a string format
     * 
     * @param millis A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
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

        return(sb.toString());
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
}