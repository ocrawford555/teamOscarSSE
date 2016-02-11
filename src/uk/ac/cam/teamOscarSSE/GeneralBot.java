package uk.ac.cam.teamOscarSSE;

import java.util.Random;

public class GeneralBot extends Bot implements Runnable {

	private static final String botName = "GenBot";

	public GeneralBot(Exchange e, Stock s) {
		super(e, s, botName);
	}

	//Call sendOrders() to automatically submit the required orders to Exchange
	@Override
	public void sendOrders() {
		int volume1 = rand.nextInt(TMAX);
		int volume2 = rand.nextInt(TMAX);
		int volume3 = rand.nextInt(TMAX);
		long stockP = stock.getStockPrice();

		long buyPrice1 = stockP - 9;        //Buy High, loses money
		long buyPrice2 = stockP + 18;
		long buyPrice3 = stockP - 6;

		Order buyOrder1 = new BuyOrder(stock, this, volume1, buyPrice1);
		Order buyOrder2 = new BuyOrder(stock, this, volume2, buyPrice2);
		Order buyOrder3 = new BuyOrder(stock, this, volume3, buyPrice3);

		long sellPrice1 = stockP + 9;
		long sellPrice2 = stockP - 18;
		long sellPrice3 = stockP + 6;

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
	public void run() {
		Random rand = new Random();
		while (super.exchange.isOpen()) {
			try {
				int nextWait = rand.nextInt(200) + 75;
				Thread.sleep(nextWait);
				this.sendOrders();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}