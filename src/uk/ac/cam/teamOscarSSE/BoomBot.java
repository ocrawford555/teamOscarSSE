package uk.ac.cam.teamOscarSSE;

public class BoomBot extends Bot implements Runnable {

	public BoomBot(Exchange2 e, Stock s, Player p) {
		super(e, s, p);
	}

	//Call sendOrders() to automatically submit the required orders to Exchange
	@Override
	public void sendOrders() {}

	@Override
	public void run() {
		while (true && !ex.isClosed()) {
			try {
				//execute something every 0.40 seconds
				Thread.sleep(200);

				int volume1 = r.nextInt(TMAX);
				int volume2 = r.nextInt(TMAX);
				int volume3 = r.nextInt(TMAX);
				long stockP = stock.getStockPrice();

				long buyPrice1 = stockP+3;		
				long buyPrice2 = stockP+7;
				long buyPrice3 = stockP+1;

				Order buyOrder1 = new BuyOrder(stock, player, volume1, buyPrice1);
				Order buyOrder2 = new BuyOrder(stock, player, volume2, buyPrice2);
				Order buyOrder3 = new BuyOrder(stock, player, volume3, buyPrice3);

				long sellPrice1 = stockP+5;
				long sellPrice2 = stockP+1;
				long sellPrice3 = stockP;

				Order sellOrder1 = new SellOrder(stock, player, volume1, sellPrice1);
				Order sellOrder2 = new SellOrder(stock, player, volume2, sellPrice2);
				Order sellOrder3 = new SellOrder(stock, player, volume3, sellPrice3);

				super.sumbitOrder(buyOrder1);
				super.sumbitOrder(buyOrder2);
				super.sumbitOrder(buyOrder3);
				super.sumbitOrder(sellOrder1);
				super.sumbitOrder(sellOrder2);
				super.sumbitOrder(sellOrder3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}