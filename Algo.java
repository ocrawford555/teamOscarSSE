package teamOscarSSE;

public class Algo {
	public static final int BUY = 1;
	public static final int SELL = 2;

	public Algo() {}

	public static void placeOrder(Stock s, double limit, int quantity, int type) {
		// TODO place an order using API or something, I don't know
	}

	public static void requestPF() {
		// TODO place API order to request portfolio
		// OR is this not required?
		// do we presume algorithm keeps correct local copy?

	}

	public void run(String choice) {
		switch(choice){
		case "Pennying":
			pennying();
			break;
		default:
			pennying();
			break;
		}
	}

	public static void pennying(){
		//run for duration of competition
		//this algorithm is VERY AGGRESIVE
		while(true){
			try {
				//execute something every 0.20 seconds
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(Stock s: StockManager.getStocks()){
				double pennyBuy = s.getBestBid();
				double pennySell = s.getBestOffer();
				//initiate pennying
				placeOrder(s,pennyBuy+1,100,BUY);
				placeOrder(s,pennySell-1,100,SELL);
			}
		}
	}
}
