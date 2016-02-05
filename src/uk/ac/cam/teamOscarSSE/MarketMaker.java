package uk.ac.cam.teamOscarSSE;

//ASSUMPTION: Market Maker has infinite Stock
public class MarketMaker implements Runnable {

	//The spread values negative if you want to buy/sell at a lower than Stock trading price
	private Exchange2 ex;
	private Stock stock;
	private long buySpread;		//Spread at which MM will issue Buy Orders at relative to current Stock Price
	private long sellSpread;	//Spread at which MM will issue Sell Orders at relative to current Stock Price
	private int volume;			//Volume of shares traded at each bid
	private Player player;		//Set to MARKET_MAKER

	//MarketMaker(Exchange object, Stock object, buyingSpread, sellingSpread, VolumeOfSocksPerOrder
	public MarketMaker(Exchange2 e, Stock s, long buySp, long sellSp, int v, Player p) {
		ex = e;
		stock = s;
		buySpread = buySp;
		sellSpread = sellSp;
		volume = v;
		player = p;
	}

	public void changeVolume(int newVolume) {
		volume = newVolume;
	}

	public void changeSpread(long newBuySpread, long newSellSpread) {
		buySpread = newBuySpread;
		sellSpread = newSellSpread;
	}

	//Submitting an order submits both Buy and Sell order at specified/default spread and volume
	public void sumbitOrder() {
		Order buyOrder = new BuyOrder(stock,player,volume,stock.getStockPrice()+buySpread);
		Order sellOrder = new SellOrder(stock,player,volume,stock.getStockPrice()+sellSpread);

		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}

	public void submitOrder(int volume) {
		Order buyOrder = new BuyOrder(stock,player,volume,stock.getStockPrice()+buySpread);
		Order sellOrder = new SellOrder(stock,player,volume,stock.getStockPrice()+sellSpread);

		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}

	public void sumbitOrder(long buySp, long sellSp, int volume) {
		Order buyOrder = new BuyOrder(stock,player,volume,stock.getStockPrice()+buySp);
		Order sellOrder = new SellOrder(stock,player,volume,stock.getStockPrice()+sellSp);
		ex.addOrder(buyOrder);
		ex.addOrder(sellOrder);
	}

	@Override
	public void run() {
		while (true && !ex.isClosed()) {
			try {
				//execute something every 0.40 seconds
				Thread.sleep(200);
				
				Order buyOrder = new BuyOrder(stock,player,volume,stock.getStockPrice()+buySpread);
				Order sellOrder = new SellOrder(stock,player,volume,stock.getStockPrice()+sellSpread);

				ex.addOrder(buyOrder);
				ex.addOrder(sellOrder);
				
				stock.newPrice();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}
