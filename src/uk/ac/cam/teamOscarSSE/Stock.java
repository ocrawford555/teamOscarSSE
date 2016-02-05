package uk.ac.cam.teamOscarSSE;

public class Stock {
	private String symbol;
	private String name;
	private long lastTransactionPrice;
	private int stockQty;
	private long bestBid;
	private long bestOffer;
	private long valueEstimate;
	
	private long stockPrice;
	private int TMAX;
	private float unc;
	
	public int getTMAX() {
		return TMAX;
	}
	
	public long getStockPrice() {
		return stockPrice;
	}
	

	public long getValueEstimate() {
		return valueEstimate;
	}

	public void setValueEstimate() {
		this.valueEstimate = (bestBid + bestOffer) / 2;
	}

	public long getBestBid() {
		return bestBid;
	}

	public void setBestBid(long bestBid) {
		this.bestBid = bestBid;
	}

	public long getBestOffer() {
		return bestOffer;
	}

	public void setBestOffer(long bestOffer) {
		this.bestOffer = bestOffer;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public long getLastTransactionPrice() {
		return lastTransactionPrice;
	}

	public void setLastTransactionPrice(long lastTransactionPrice) {
		this.lastTransactionPrice = lastTransactionPrice;
	}

	public int getStockQty() {
		return stockQty;
	}

	public void setStockQty(int amountToAdd) {
		this.stockQty += amountToAdd;
	}

	public void addStockQty(int amountToAdd) {
		this.stockQty += amountToAdd;
	}

	public void removeStockQty(int amountToRemove) {
		this.stockQty -= amountToRemove;
	}

	public Stock(String sym, String nameCompany, int quantity, float uncertainty) {
		this.symbol = sym;
		this.name = nameCompany;
		this.setStockQty(quantity);
		this.unc = uncertainty;
	}
	
	//Calculates new Stock price given volume of stock traded and trading price
	public void newPrice(long tPrice, float volume) {
		float tradePrice = ((float) tPrice)/100;
		float price = ((float) stockPrice)/100;
		float sigmaS = unc;
		float temp = TMAX/volume -1;
		float sigmaE = sigmaS*temp;
		float k = (sigmaS*sigmaS)/((sigmaS*sigmaS)+(sigmaE*sigmaE));
		price = price + (k*(tradePrice - price));
		stockPrice = (long) price*100;
		float temp2 = 1 - (k*sigmaS);
		unc = (float) Math.sqrt(temp2); //New uncertainty
		
	}
}
