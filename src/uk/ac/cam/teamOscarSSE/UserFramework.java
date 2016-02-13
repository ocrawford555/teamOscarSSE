package uk.ac.cam.teamOscarSSE;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class UserFramework implements Runnable {
	
	private Stock stock;
	private Exchange ex;
	
	public UserFramework(Stock s, Exchange e) {
		stock = s;
		ex = e;
	}
	
	Random r = new Random();
	private Player p = new Player("User"+r.nextInt(100),"test@test.com");
	//Change these to reflect HTTP Server format and all the get methods
	
	private long stockPrice;
	private List<Long> pointAvg = new LinkedList<Long>();
	private long overallAvg;
	private List<Long> transactionAvg = new LinkedList<Long>();
	private List<Float> rateOfChange = new LinkedList<Float>();
	private long cash;
	
	
	public boolean Buy(){
		return false;
	}
	
	public int volumeToBuy() {
		return 0;
	}
	
	public boolean Sell(){
		return false;
	}
	
	public int volumeToSell() {
		return 0;
	}
	
	public void submitBuyOrder() {
		BuyOrder buy = new BuyOrder(stock,p,volumeToBuy(),stockPrice);
		ex.addOrder(buy);
	}
	
	public void submitSellOrder() {
		SellOrder buy = new SellOrder(stock,p,volumeToSell(),stockPrice);
		ex.addOrder(buy);
	}
	
	public void update() {
		stockPrice = stock.getStockPrice();
		
		pointAvg = stock.getPointAvg();
		
		overallAvg = stock.getOverallAverage();

		transactionAvg = stock.getTransactionAvg();

		rateOfChange = stock.getRateOfChange();
		
		//cash = stock.getCash();
	}

	@Override
	public void run() {
		boolean shouldBuy = false;
		boolean shouldSell = false; 
		while (true) {
			update();
			shouldBuy = Buy();
			shouldSell = Sell();
			if (shouldBuy) submitBuyOrder();
			if (shouldSell) submitSellOrder();
		}
	}
	
	
	 
	
	
	

}
