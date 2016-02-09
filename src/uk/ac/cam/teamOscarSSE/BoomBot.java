package uk.ac.cam.teamOscarSSE;

import java.util.Random;

public class BoomBot extends Bot implements Runnable {

	private Player playerID = new Player("BomBot", "abc@abc.com");

	public BoomBot(Exchange e, Stock s) {
		super(e, s);
		e.addPlayer(playerID);
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
		
		Order buyOrder1 = new BuyOrder(stock, playerID, volume1, buyPrice1);
		Order buyOrder2 = new BuyOrder(stock, playerID, volume2, buyPrice2);
		Order buyOrder3 = new BuyOrder(stock, playerID, volume3, buyPrice3);

		long sellPrice1 = stockP+200;
		long sellPrice2 = stockP+400;
		long sellPrice3 = stockP;
		
		Order sellOrder1 = new SellOrder(stock, playerID, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(stock, playerID, volume2, sellPrice2);
		Order sellOrder3 = new SellOrder(stock, playerID, volume3, sellPrice3);

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
		while(super.ex.isOpen()){
			try{
				int nextWait = rand.nextInt(200) + 75;
				Thread.sleep(nextWait);
				this.sendOrders();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}