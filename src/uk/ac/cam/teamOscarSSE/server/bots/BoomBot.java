package uk.ac.cam.teamOscarSSE.server.bots;

import uk.ac.cam.teamOscarSSE.server.*;

import java.util.Random;

/**
 * BoomBot designed to increase price of stock rapidly.
 * Achieves this by buying high in order to get stocks,
 * and sell only at a high price.
 */
public class BoomBot extends Bot implements Runnable {

	//Name of bot
	private static final String botName = "BoomBot";

	/**
	 * 
	 * @param e - Exchange being used.
	 * @param s - Stock the bot is trading with.
	 */
	public BoomBot(Exchange e, Stock s) {
		this(e, s, botName);
	}
	
	/**
	 * 
	 * @param e - Exchange being used.
	 * @param s - Stock the bot is trading with.
	 * @param botName - name of bot
	 */
	public BoomBot(Exchange e, Stock s, String botName) {
		super(e, s, botName);
	}

	@Override
	/**
	 * Sends orders to the exchange.
	 */
	public void sendOrders() {
		int volume1 = rand.nextInt(TMAX);
		int volume2 = rand.nextInt(TMAX);
		int volume3 = rand.nextInt(TMAX);
		long stockP = stock.getStockPrice();

		long buyPrice1 = stockP + 125 + rand.nextInt(5);
		long buyPrice2 = stockP + 170 + rand.nextInt(15);
		long buyPrice3 = stockP + 50;

		Order buyOrder1 = new BuyOrder(stock, this, volume1, buyPrice1);
		Order buyOrder2 = new BuyOrder(stock, this, volume2, buyPrice2);
		Order buyOrder3 = new BuyOrder(stock, this, volume3, buyPrice3);

		long sellPrice1 = stockP + 40 + rand.nextInt(5);
		long sellPrice2 = stockP + 20;

		Order sellOrder1 = new SellOrder(stock, this, volume1/2, sellPrice1);
		Order sellOrder2 = new SellOrder(stock, this, volume2/2, sellPrice2);

		super.submitOrder(buyOrder1);
		super.submitOrder(buyOrder2);
		super.submitOrder(buyOrder3);
		super.submitOrder(sellOrder1);
		submitOrder(sellOrder2);
	}

	@Override
	/**
	 * Orders are submitted at random intervals
	 * for increased market realism. Loops until the
	 * exchange is closed and the round is over.
	 */
	public void run() {
		Random rand = new Random();
		while (super.exchange.isOpen()) {
			try {
				int nextWait = rand.nextInt(100) + 25;
				Thread.sleep(nextWait);
				this.sendOrders();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}