package uk.ac.cam.teamOscarSSE;


import uk.ac.cam.teamOscarSSE.BuyOrder;
import uk.ac.cam.teamOscarSSE.Order;
import uk.ac.cam.teamOscarSSE.OrderType;
import uk.ac.cam.teamOscarSSE.SellOrder;
import uk.ac.cam.teamOscarSSE.Stock;

public class BoomBot extends Bot {

	public BoomBot(Exchange e, Stock s) {
		super(e, s);
	}
	
	//Call sendOrders() to automatically submit the required orders to Exchange
	@Override
	public void sendOrders() {
		int volume1 = r.nextInt(TMAX);
		int volume2 = r.nextInt(TMAX);
		int volume3 = r.nextInt(TMAX);
		long stockP = stock.getStockPrice();
		
		long buyPrice1 = stockP+300;		
		long buyPrice2 = stockP+700;
		long buyPrice3 = stockP+100;
		
		Order buyOrder1 = new BuyOrder(OrderType.BUY, "BomBot", stock, volume1, buyPrice1);
		Order buyOrder2 = new BuyOrder(OrderType.BUY, "BomBot", stock, volume2, buyPrice2);
		Order buyOrder3 = new BuyOrder(OrderType.BUY, "BomBot", stock, volume3, buyPrice3);

		long sellPrice1 = stockP+500;
		long sellPrice2 = stockP+100;
		long sellPrice3 = stockP;
		
		Order sellOrder1 = new SellOrder(OrderType.SELL, "BomBot", stock, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(OrderType.SELL, "BomBot", stock, volume2, sellPrice2);
		Order sellOrder3 = new SellOrder(OrderType.SELL, "BomBot", stock, volume3, sellPrice3);

		super.sumbitOrder(buyOrder1);
		super.sumbitOrder(buyOrder2);
		super.sumbitOrder(buyOrder3);
		super.sumbitOrder(sellOrder1);
		super.sumbitOrder(sellOrder2);
		super.sumbitOrder(sellOrder3);
		
	}
	
}
