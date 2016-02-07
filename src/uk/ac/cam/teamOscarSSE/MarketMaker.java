package uk.ac.cam.teamOscarSSE;

//ASSUMPTION: Market Maker has infinite Stock
public class MarketMaker implements Runnable {

	//The spread values negative if you want to buy/sell at a lower than Stock trading price
	private Exchange ex;
	private Stock stock;
	private long buySpread;		//Spread at which MM will issue Buy Orders at relative to current Stock Price
	private long sellSpread;	//Spread at which MM will issue Sell Orders at relative to current Stock Price
	private int volume;			//Volume of shares traded at each bid
	private Player playerID = new Player("MarketMaker", "abc@abc.com");
	
	//MarketMaker(Exchange object, Stock object, buyingSpread, sellingSpread, VolumeOfSocksPerOrder
	public MarketMaker(Exchange e, Stock s, long buySp, long sellSp, int v) {
		ex = e;
		stock = s;
		buySpread = buySp;
		sellSpread = sellSp;
		volume = v;
		ex.addPlayer(playerID);
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
		
		Order buyOrder = new BuyOrder(stock, playerID, volume, stock.getStockPrice()+buySpread);
		Order sellOrder = new SellOrder(stock, playerID, volume, stock.getStockPrice()+sellSpread);
		
		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}
	
	public void submitOrder(int volume) {
		Order buyOrder = new BuyOrder(stock, playerID, volume, stock.getStockPrice()+buySpread);
		Order sellOrder = new SellOrder(stock, playerID, volume, stock.getStockPrice()+sellSpread);
		
		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}
	
	public void submitOrder(long buySp, long sellSp, int volume) {
		Order buyOrder = new BuyOrder(stock, playerID, volume, stock.getStockPrice()+buySp);
		Order sellOrder = new SellOrder(stock, playerID, volume, stock.getStockPrice()+sellSp);
		
		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
		
	}

	@Override
	public void run() {
		while(ex.isOpen()){
			try{
				Thread.sleep(150);
				this.submitOrder();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}