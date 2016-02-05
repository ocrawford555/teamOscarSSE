package uk.ac.cam.teamOscarSSE;


public class RecessionBot extends Bot {

	private Player playerID = new Player("RecBot", "abc@abc.com"); 
	
	public RecessionBot(Exchange e, Stock s) {
		super(e, s);
	}
	
	
	//Call sendOrders() to automatically submit the required orders to Exchange
	@Override
	public void sendOrders() {
		int volume1 = r.nextInt(TMAX);
		int volume2 = r.nextInt(TMAX);
		long stockP = stock.getStockPrice();
		
		long buyPrice1 = stockP+300;		
		
		Order buyOrder1 = new BuyOrder(stock, playerID, volume1, buyPrice1);

		long sellPrice1 = stockP-100;
		long sellPrice2 = stockP-400;
		
		Order sellOrder1 = new SellOrder(stock, playerID, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(stock, playerID, volume2, sellPrice2);

		super.sumbitOrder(buyOrder1);
		super.sumbitOrder(sellOrder1);
		super.sumbitOrder(sellOrder2);
	}
	
}
