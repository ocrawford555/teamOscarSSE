package uk.ac.cam.teamOscarSSE;

import java.util.Random;

public class PriceMovingBot extends Bot implements Runnable {
	private static final String botName = "PMBot";

	public PriceMovingBot(Exchange e, Stock s) {
		super(e, s, botName);
	}

	@Override
	public void run() {
		Random rand = new Random();
		while (super.exchange.isOpen()) {
			try {
				int nextWait = rand.nextInt(150) + 40;
				Thread.sleep(nextWait);
				this.sendOrders();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendOrders() {
		//if price are going up then sell, simple, right?
		long stockP = stock.getStockPrice();

		if (stock.getPointAvg5() < stock.getPointAvg20()) {
			int volume1 = rand.nextInt(TMAX);
			long buyPrice1 = stockP + rand.nextInt(25);

			Order buyOrder1 = new BuyOrder(stock, this, volume1, buyPrice1);

			if (stock.getPointAvg20() < stock.getPointAvg50()) {
				int volume2 = rand.nextInt(TMAX);
				long buyPrice2 = stockP + rand.nextInt(80);
				Order buyOrder2 = new BuyOrder(stock, this, volume2, buyPrice2);
				super.submitOrder(buyOrder2);

			}
			super.submitOrder(buyOrder1);
		}

		//if price is going down, then buy, surely?
		if (stock.getPointAvg5() > stock.getPointAvg20()) {
			int volume1 = rand.nextInt(TMAX);
			long sellPrice1 = stockP - rand.nextInt(40);

			Order sellOrder1 = new SellOrder(stock, this, volume1, sellPrice1);

			if (stock.getPointAvg20() > stock.getPointAvg50()) {
				int volume2 = rand.nextInt(TMAX);
				long sellPrice2 = stockP - rand.nextInt(100);
				Order sellOrder2 = new SellOrder(stock, this, volume2, sellPrice2);
				super.submitOrder(sellOrder2);
			}

			super.submitOrder(sellOrder1);
		}
	}
}
