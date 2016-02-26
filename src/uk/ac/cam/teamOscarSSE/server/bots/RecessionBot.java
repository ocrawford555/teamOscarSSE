package uk.ac.cam.teamOscarSSE.server.bots;

import uk.ac.cam.teamOscarSSE.server.*;

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

		long buyPrice1 = stockP - 100 - rand.nextInt(50);
		long buyPrice2 = stockP - 100;

		Order buyOrder1 = new BuyOrder(stock, this, volume1, buyPrice1);
		Order buyOrder2 = new BuyOrder(stock, this, volume2, buyPrice2);

		long sellPrice1 = stockP - 200;
		long sellPrice2 = stockP - 50 - rand.nextInt(200);
		
		Order sellOrder1 = new SellOrder(stock, this, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(stock, this, volume2, sellPrice2);

		super.submitOrder(buyOrder1);
		super.submitOrder(buyOrder2);
		super.submitOrder(sellOrder1);
		super.submitOrder(sellOrder2);
	}


	@Override
	public void run() {
		Random rand = new Random();
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