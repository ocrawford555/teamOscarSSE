package uk.ac.cam.teamOscarSSE;

import java.util.Random;

public class RecessionBot extends Bot implements Runnable {

	private static final String botName = "RecBot";

	public RecessionBot(Exchange e, Stock s) {
		super(e, s, botName);
	}


	//Call sendOrders() to automatically submit the required orders to Exchange
	@Override
	public void sendOrders() {
		int volume1 = rand.nextInt(TMAX);
		int volume2 = rand.nextInt(TMAX);
		long stockP = stock.getStockPrice();

		long buyPrice1 = stockP + 800;

		Order buyOrder1 = new BuyOrder(stock, this, volume1, buyPrice1);

		long sellPrice1 = stockP - 5;
		long sellPrice2 = stockP - 17;

		Order sellOrder1 = new SellOrder(stock, this, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(stock, this, volume2, sellPrice2);

		super.submitOrder(buyOrder1);
		super.submitOrder(sellOrder1);
		super.submitOrder(sellOrder2);
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