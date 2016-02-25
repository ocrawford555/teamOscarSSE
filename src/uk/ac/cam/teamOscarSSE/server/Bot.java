package uk.ac.cam.teamOscarSSE.server;

import java.util.Random;

/**
 * A bot running on an exchange.
 */
public abstract class Bot extends Trader implements Runnable {

	// The stock traded by the bot.
	protected Stock stock;

	// The exchange on which the bot trades.
	protected Exchange exchange;

	// A random number generator used in randomizing a bot's actions.
	protected Random rand = new Random();

	//Transaction maximum value
	protected int TMAX;

	/**
	 * A bot trades with a given stock on the exchange.
	 * A bot is automatically added to the exchange upon construction.
	 *
	 * @param exchange
	 * @param stock
	 * @param botName
	 */
	public Bot(Exchange exchange, Stock stock, String botName) {
		super(botName, "bot@bot.com");
		this.exchange = exchange;
		this.stock = stock;
		TMAX = stock.getTMAX();
		this.exchange.addPlayer(this);
	}

	/**
	 * Returns the maximum integer, as a bot can buy an unlimited amount.
	 *
	 * @param stock
	 * @param price
	 * @return
	 */
	@Override
	public int maxCanBuy(Stock stock, long price) {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns the maximum integer, as a bot can sell an unlimited amount.
	 *
	 * @param stock
	 * @return
	 */
	public int maxCanSell(Stock stock) {
		return Integer.MAX_VALUE;
	}

	/**
	 * Submits an order to the exchange.
	 *
	 * @param order
	 * @return
	 */
	public boolean submitOrder(Order order) {
		OrderChangeMessage msg = exchange.addOrder(order);
		return !(msg == null || msg.getType() == OrderChangeMessage.ChangeType.FAIL);
	}

	/**
	 * Abstract method that sends an order to the exchange.
	 */
	public abstract void sendOrders();
}
