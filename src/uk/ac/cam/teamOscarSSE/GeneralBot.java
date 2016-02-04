package uk.ac.cam.teamOscarSSE;

public class GeneralBot extends Bot {
	
	public GeneralBot(Exchange e, Stock s) {
		super(e, s);
	}
	
	
	//Call sendOrders() to automatically submit the required orders to Exchange
	@Override	
	public void sendOrders() {
		int volume1 = r.nextInt(TMAX);
		int volume2 = r.nextInt(TMAX);
		int volume3 = r.nextInt(TMAX);
		long stockP = stock.getStockPrice();
		
		long buyPrice1 = stockP-300;		//Buy High, loses money
		long buyPrice2 = stockP+600;
		long buyPrice3 = stockP-200;
		
		Order buyOrder1 = new BuyOrder(OrderType.BUY, "GenBot", stock, volume1, buyPrice1);
		Order buyOrder2 = new BuyOrder(OrderType.BUY, "GenBot", stock, volume2, buyPrice2);
		Order buyOrder3 = new BuyOrder(OrderType.BUY, "GenBot", stock, volume3, buyPrice3);

		long sellPrice1 = stockP+300;
		long sellPrice2 = stockP-600;
		long sellPrice3 = stockP+200;
		
		Order sellOrder1 = new SellOrder(OrderType.SELL, "GenBot", stock, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(OrderType.SELL, "GenBot", stock, volume2, sellPrice2);
		Order sellOrder3 = new SellOrder(OrderType.SELL, "GenBot", stock, volume3, sellPrice3);

		super.sumbitOrder(buyOrder1);
		super.sumbitOrder(buyOrder2);
		super.sumbitOrder(buyOrder3);
		super.sumbitOrder(sellOrder1);
		super.sumbitOrder(sellOrder2);
		super.sumbitOrder(sellOrder3);
		
	}

	
	
	
}
