package uk.ac.cam.teamOscarSSE;


public class RecessionBot extends Bot implements Runnable {

	public RecessionBot(Exchange2 e, Stock s, Player p) {
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
				Thread.sleep(250);

				int volume1 = r.nextInt(TMAX);
				int volume2 = r.nextInt(TMAX);
				long stockP = stock.getStockPrice();

				long buyPrice1 = stockP+3;		

				Order buyOrder1 = new BuyOrder(stock, player, volume1, buyPrice1);

				long sellPrice1 = stockP-1;
				long sellPrice2 = stockP-4;

				Order sellOrder1 = new SellOrder(stock, player, volume1, sellPrice1);
				Order sellOrder2 = new SellOrder(stock, player, volume2, sellPrice2);

				super.sumbitOrder(buyOrder1);
				super.sumbitOrder(sellOrder1);
				super.sumbitOrder(sellOrder2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}
