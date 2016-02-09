package uk.ac.cam.teamOscarSSE;

import java.util.Random;

public class RecessionBot extends Bot implements Runnable {

	private Player playerID = new Player("RecBot", "abc@abc.com"); 
	
	public RecessionBot(Exchange e, Stock s) {
		super(e, s);
		e.addPlayer(playerID);
	}
	
	
	//Call sendOrders() to automatically submit the required orders to Exchange
	@Override
	public void sendOrders() {
		int volume1 = r.nextInt(TMAX);
		int volume2 = r.nextInt(TMAX);
		long stockP = stock.getStockPrice();
		
		long buyPrice1 = stockP+800;		
		
		Order buyOrder1 = new BuyOrder(stock, playerID, volume1, buyPrice1);

		long sellPrice1 = stockP-5;
		long sellPrice2 = stockP-17;
		
		Order sellOrder1 = new SellOrder(stock, playerID, volume1, sellPrice1);
		Order sellOrder2 = new SellOrder(stock, playerID, volume2, sellPrice2);

		super.submitOrder(buyOrder1);
		super.submitOrder(sellOrder1);
		super.submitOrder(sellOrder2);
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