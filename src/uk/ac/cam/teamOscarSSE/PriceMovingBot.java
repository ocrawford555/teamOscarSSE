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
				int nextWait = rand.nextInt(150) + 25;
				Thread.sleep(nextWait);
				this.sendOrders();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	@Override
	public void sendOrders() {
		//if price are going up then buy, simple, right?
		long stockP = stock.getStockPrice();

		if (stock.getPointAvg().get(5) < stock.getPointAvg().get(20)) {
			int volume1 = rand.nextInt(TMAX);
			long buyPrice1 = stockP + rand.nextInt(50);

			Order buyOrder1 = new BuyOrder(stock, this, volume1, buyPrice1);

			if (stock.getPointAvg().get(20) < stock.getPointAvg().get(50)) {
				int volume2 = rand.nextInt(TMAX);
				long buyPrice2 = stockP + rand.nextInt(150);
				Order buyOrder2 = new BuyOrder(stock, this, volume2, buyPrice2);
				super.submitOrder(buyOrder2);

			}
			super.submitOrder(buyOrder1);
		}

		//if price is going down, then sell, surely?
		if (stock.getPointAvg().get(5) > stock.getPointAvg().get(20)) {
			int volume1 = rand.nextInt(TMAX);
			long sellPrice1 = stockP - rand.nextInt(50);

			Order sellOrder1 = new SellOrder(stock, this, volume1, sellPrice1);

			if (stock.getPointAvg().get(20) > stock.getPointAvg().get(50)) {
				int volume2 = rand.nextInt(TMAX);
				long sellPrice2 = stockP - rand.nextInt(150);
				Order sellOrder2 = new SellOrder(stock, this, volume2, sellPrice2);
				super.submitOrder(sellOrder2);
			}
			super.submitOrder(sellOrder1);
		}
	}
}
