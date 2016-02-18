package uk.ac.cam.teamOscarSSE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A Trader is any participant that issues orders to the Exchange.
 * <p>
 * For instance, users or bots can subclass Trader.
 */
public abstract class Trader {

	// The name of the competitor.
	protected final String name;

	// The unique ID generated at creation of an instance of a Trader.
	protected final String token;
	protected final String emailAddress;

	// The portfolio of stocks owned.
	protected Portfolio pf;

	// The portfolio containing pending sell orders.
	protected Portfolio pending_pf;

	// The pending orders successfully submitted to the Exchange.
	protected Map<Long, Order> pending_orders;

	// The amount of cash owned by the trader at any given time.
	protected long cash;

	// The cash blocked due to pending buy orders.
	protected long cashBlocked;

	Trader(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
		this.token = generateRandomID();
		this.pf = new Portfolio();
		this.pending_orders = new HashMap<>();
		this.pending_pf = new Portfolio();
		this.cash = 0;
		this.cashBlocked = 0;
	}

	/**
	 * Reset the trader's cash and portfolio.
	 * <p>
	 * This should be called when a new round is started.
	 */
	public void reset() {
		this.pf = new Portfolio();
		this.pending_orders = new HashMap<>();
		this.pending_pf = new Portfolio();
		this.cash = 0;
		this.cashBlocked = 0;
	}

	/**
	 * Generates a random 32 hex characters (128 bit) token.
	 *
	 * @return
	 */
	private final String generateRandomID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * Returns the trader's name.
	 *
	 * @return
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the trader's unique token.
	 *
	 * @return The unique token identifier.
	 */
	public final String getToken() {
		return token;
	}

	public final String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Returns the balance of the trader, which is the cash owned and the
	 * current value of the player's portfolio.
	 *
	 * @return the current balance of the trader.
	 */
	public final long getBalance() {
		return cash + pf.currentValue();
	}

	/**
	 * Gets the portfolio of stocks owned by the player.
	 *
	 * @return the Portfolio of stocks owned by the player.
	 */
	public Portfolio getPortfolio() {
		return pf;
	}

	/**
	 * Returns the maximum amount of stock a trader can buy at a given price.
	 *
	 * @param stock
	 * @param price
	 * @return
	 */
	public abstract int maxCanBuy(Stock stock, long price);

	/**
	 * Returns the maximum number of stock a player can sell.
	 *
	 * @param stock
	 * @return
	 */
	public abstract int maxCanSell(Stock stock);

	/**
	 * @return orders pending in the exchange.
	 */
	public synchronized final Map<Long, Order> getPendingOrders() {
		return pending_orders;
	}
	
	/**
	 * Updates the user's cash by adding cashChange to their owned cash.
	 * <p>
	 * This method can both increase and decrease the amount of cash owned based
	 * on the sign of cashChange.
	 *
	 * @param cashChange
	 */
	public final void updateCash(long cashChange) {
		this.cash += cashChange;
	}
	
	/**
	 * Returns the remaining cash Trader has
	 * @return
	 */
	public final long returnCash() {
		return cash;
	}

	/**
	 * @param orderNum
	 * @return true if the trader has the order pending.
	 */
	public synchronized final boolean hasOrderPending(Long orderNum) {
		return pending_orders.containsKey(orderNum);
	}

	/**
	 * Removes a pending order.
	 * This updates a player's pending_pf.
	 *
	 * @param orderNum
	 * @return true if successfully removed, false otherwise (e.g. order does not exist).
	 */
	public synchronized boolean removeOrder(Long orderNum) {
		Order order = pending_orders.get(orderNum);
		if (order == null) {
			return false;
		}
		if (order instanceof BuyOrder) {
			// Unblock player's cash
			cashBlocked -= order.getShares() * order.getPrice();
		} else if (order instanceof SellOrder) {
			// Unblock player's stocks in pending_pf
			pending_pf.remove(order.getStock(), order.getShares());
		} else {
			System.err.println("Order type not implemented.");
			return false;
		}
		pending_orders.remove(orderNum);
		return true;
	}

	/**
	 * Adds a new buy order.
	 * This blocks the amount of cash equal to BuyOrder.price * BuyOrder.shares.
	 *
	 * @param order
	 * @return true if succeeded, false otherwise.
	 */
	public boolean addPendingOrder(BuyOrder order) {
		cashBlocked += order.getPrice() * order.getShares();
		pending_orders.put(order.getOrderNum(), order);
		return true;
	}

	/**
	 * Adds a new sell order.
	 * This blocks the amount of stock equal to order.shares.
	 *
	 * @param order
	 * @return true if succeeded, false otherwise.
	 */
	public boolean addPendingOrder(SellOrder order) {
		pending_pf.add(order.getStock(), order.getShares());
		pending_orders.put(order.getOrderNum(), order);
		return true;
	}

	/**
	 * Updates a user's portfolio based on the change in orderUpdate.
	 *
	 * @param orderUpdate
	 */
	public void updatePortfolio(OrderUpdateMessage orderUpdate) {
		Stock tradedStock = orderUpdate.order.getStock();

		if (orderUpdate.order.getOrderType() == OrderType.BUY) {
			pf.add(tradedStock, orderUpdate.size);
			//as player is buying stocks, they are spending money
			updateCash(-1 * (orderUpdate.size * orderUpdate.price));

			// Unblock cash
			cashBlocked += -1 * (orderUpdate.size * orderUpdate.price);
		} else if (orderUpdate.order.getOrderType() == OrderType.SELL) {
			pf.remove(tradedStock, orderUpdate.size);
			//as player is selling stocks, they gain cash
			updateCash(orderUpdate.size * orderUpdate.price);

			// Unblock shares
			pending_pf.remove(orderUpdate.order.getStock(), orderUpdate.getSize());
		} else {
			System.err.println("Unimplemented order type");
		}
	}
}

