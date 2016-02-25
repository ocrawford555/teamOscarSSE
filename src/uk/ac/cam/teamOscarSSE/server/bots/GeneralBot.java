package uk.ac.cam.teamOscarSSE.server.bots;

import uk.ac.cam.teamOscarSSE.server.*;

import java.util.Random;

/**
 * GeneralBot is designed to do very general, not exciting trading.
 * Achieves this by sending relatively harmless orders.
 */
public class GeneralBot extends Bot implements Runnable {

	//Name of this bot.
	private static final String botName = "GenBot";
	
	public GeneralBot(Exchange e, Stock s) {
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

		long buyPrice1;
		long buyPrice2;
		long buyPrice3;
		
		long sellPrice1;
		long sellPrice2;
		long sellPrice3;
		
		buyPrice1 = stockP - 300;        //Buy High, loses money
		buyPrice2 = stockP + 600;
		buyPrice3 = stockP - 200;
		
		sellPrice1 = stockP + 300;
		sellPrice2 = stockP - 600;
		sellPrice3 = stockP + 200;

		Order buyOrder1 = new BuyOrder(stock, this, volume1, buyPrice1);
		Order buyOrder2 = new BuyOrder(stock, this, volume2, buyPrice2);
		Order buyOrder3 = new BuyOrder(stock, this, volume3, buyPrice3);



		Order sellOrder1 = new SellOrder(stock, this, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(stock, this, volume2, sellPrice2);
		Order sellOrder3 = new SellOrder(stock, this, volume3, sellPrice3);

		super.submitOrder(buyOrder1);
		super.submitOrder(buyOrder2);
		super.submitOrder(buyOrder3);
		super.submitOrder(sellOrder1);
		super.submitOrder(sellOrder2);
		super.submitOrder(sellOrder3);

	}

	@Override
	/**
	 * Orders are submitted at random intervals
	 * for increased market realism. Loops until the
	 * exchange is closed and the round is over.
	 */
	public void run() {
		Random rand = new Random();
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		while (super.exchange.isOpen()) {
			try {
				if (Thread.interrupted()) return;
				int nextWait = rand.nextInt(100);
				Thread.sleep(nextWait);
				this.sendOrders();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}