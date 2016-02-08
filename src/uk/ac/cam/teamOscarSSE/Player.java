package uk.ac.cam.teamOscarSSE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Player {
	//human name of competitor
	private String name;

	//unique ID matched to competitor, generated
	//at creation of an instance of User object
	private String token;

	//email address of the user
	private String emailAddress;

	//amount of cash owned by the player at any given time
	private long cashLeft;

	// cash blocked due to pending buy orders
	private long cashBlocked;

	//link to portfolio of stocks owned
	private Portfolio pf;

	private Map<Long, Order> pending_orders;

	// Portfolio containing pending orders.
	private Portfolio pending_pf;

	//private Algo algo;

	public String getName() {
		return name;
	}

	public String getToken() {
		return token;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public long getBalance() {
		return cashLeft + pf.currentValue();
	}

	/**
	 * Returns the maximum amount of a stock a player can buy at a given price.
	 *
	 * @param stock
	 * @param price
	 * @return
	 */
	public long maxCanBuy(Stock stock, long price) {
		if (price == 0) {
			return 0;
		}
		return (cashLeft - cashBlocked) / price;
	}

	/**
	 * Returns the maximum number of this stock a player can sell.
	 * Currently this is equal to the amount of stock owned.
	 *
	 * @param stock
	 * @return
	 */
	public long maxCanSell(Stock stock) {
		return pf.getAmountOwned(stock) - pending_pf.getAmountOwned(stock);
	}

	public Portfolio getPortfoio() {
		return pf;
	}

	/**
	 * Remove pending order from player's tracking.
	 * This is necessary for accurate maxCanBuy and maxCanSell.
	 *
	 * @param orderNum
	 * @return
	 */
	public boolean removeOrder(Long orderNum) {
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

	public boolean hasOrderPending(Long orderNum) {
		return pending_orders.containsKey(orderNum);
	}

	/**
	 * Player has a buy order pending. Blocks cash equal to order.price * order.shares.
	 *
	 * @param order
	 * @return
	 */
	public boolean addPendingOrder(BuyOrder order) {
		cashBlocked += order.getPrice() * order.getShares();
		pending_orders.put(order.getOrderNum(), order);
		return true;
	}

	/**
	 * Player has a sell order pending. Blocks amount of stock equal to order.shares.
	 *
	 * @param order
	 * @return
	 */
	public boolean addPendingOrder(SellOrder order) {
		// TODO: semantically this does not make sense.
		pending_pf.add(order.getStock(), order.getShares());
		pending_orders.put(order.getOrderNum(), order);
		return true;
	}

	// TODO: argument won't actually be an OrderUpdateMessage.
	public void updatePortfolio(OrderUpdateMessage orderUpdate) {
		Stock tradedStock = orderUpdate.order.getStock();

		if (orderUpdate.order.getOrderType() == OrderType.BUY) {
			pf.add(tradedStock, orderUpdate.size);
			//as player is buying stocks, they are spending money
			updateCash(-1 * (orderUpdate.size * orderUpdate.price));
			//System.out.println("Cash lost: " + (-1 * (orderUpdate.size * orderUpdate.price)));
		} else if (orderUpdate.order.getOrderType() == OrderType.SELL) {
			pf.remove(tradedStock, orderUpdate.size);
			//as player is selling stocks, they gain cash
			updateCash(orderUpdate.size * orderUpdate.price);
			//System.out.println("Cash gained: " + (orderUpdate.size * orderUpdate.price));
		} else {
			System.err.println("Unimplemented order type");
		}
	}

	//this method can both increase cash or decrease cash
	//depending on sign
	public void updateCash(long cash) {
		cashLeft += cash;
	}

	public Player(String name, String email) {
		this.name = name;
		this.emailAddress = email;
		//generate random 32 hex characters (128 bit) token
		this.token = UUID.randomUUID().toString().replaceAll("-", "");
		//start with 10,000,000 cents (or pennies, depending on currency)
		this.cashLeft = 10000000;
		this.pf = new Portfolio();
		this.pending_orders = new HashMap<>();
		this.pending_pf = new Portfolio();
		this.cashBlocked = 0;
	}
}
