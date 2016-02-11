package uk.ac.cam.teamOscarSSE;

import java.util.Random;

//ASSUMPTION: Market Maker has infinite Stock
public class MarketMaker extends Bot implements Runnable {

	private static final String botName = "MarketMaker";
	//The spread values negative if you want to buy/sell at a lower than Stock trading price
	private long buySpread;		//Spread at which MM will issue Buy Orders at relative to current Stock Price
	private long sellSpread;	//Spread at which MM will issue Sell Orders at relative to current Stock Price
	private int volume;			//Volume of shares traded at each bid
	
	//MarketMaker(Exchange object, Stock object, buyingSpread, sellingSpread, VolumeOfSocksPerOrder
	public MarketMaker(Exchange e, Stock s, long buySp, long sellSp, int v) {
		super(e, s, botName);
		buySpread = buySp;
		sellSpread = sellSp;
		volume = v;
	}
	
	public void changeVolume(int newVolume) {
		volume = newVolume;
	}
	
	public void changeSpread(long newBuySpread, long newSellSpread) {
		buySpread = newBuySpread;
		sellSpread = newSellSpread;
	}
	
	//Submitting an order submits both Buy and Sell order at specified/default spread and volume
	public void submitOrder() {
		Order buyOrder = new BuyOrder(stock, this, volume, stock.getStockPrice() + buySpread);
		Order sellOrder = new SellOrder(stock, this, volume, stock.getStockPrice() + sellSpread);
		
		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}
	
	public void submitOrder(int volume) {
		Order buyOrder = new BuyOrder(stock, this, volume, stock.getStockPrice() + buySpread);
		Order sellOrder = new SellOrder(stock, this, volume, stock.getStockPrice() + sellSpread);
		
		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}
	
	public void submitOrder(long buySp, long sellSp, int volume) {
		Order buyOrder = new BuyOrder(stock, this, volume, stock.getStockPrice() + buySp);
		Order sellOrder = new SellOrder(stock, this, volume, stock.getStockPrice() + sellSp);
		
		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}

	@Override
	public void sendOrders() {
		submitOrder();
	}

	@Override
	public void run() {
		Random rand = new Random();
		while(ex.isOpen()){
			try{
				int nextWait = rand.nextInt(200) + 75;
				Thread.sleep(nextWait);
				// TODO: I think we should let only the Exchange call stock.newPrice()
				stock.newPrice();
				this.sendOrders();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}