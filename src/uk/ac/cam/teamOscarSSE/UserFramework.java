package uk.ac.cam.teamOscarSSE;

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
	
	private long pointAvg5;
	private long pointAvg20;
	private long pointAvg50;
	
	private long overallAvg;

	private long transactionAvg5;
	private long transactionAvg20;
	private long transactionAvg50;

	//Change in stock price
	private float rateOfChange5;
	private float rateOfChange20;
	private float rateOfChange50;
	
	//User statistics
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
		
		pointAvg5 = stock.getPointAvg5();
		pointAvg20 = stock.getPointAvg20();
		pointAvg50 = stock.getPointAvg50();
		
		overallAvg = stock.getOverallAverage();

		transactionAvg5 = stock.getTransactionAvg5();
		transactionAvg20 = stock.getTransactionAvg20();
		transactionAvg50 = stock.getTransactionAvg50();

		//Change in stock price
		rateOfChange5 = stock.getRateOfChange5();
		rateOfChange20 = stock.getRateOfChange20();
		rateOfChange50 = stock.getRateOfChange50();
		
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
