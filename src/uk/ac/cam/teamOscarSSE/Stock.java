package uk.ac.cam.teamOscarSSE;

public class Stock {
	private String symbol;
	private String name;
	private long lastTransactionPrice;
	private long volumeTraded;
	private int stockQty;
	private long bestBid;
	private long bestOffer;
	
	private long stockPrice;
	private int TMAX;
	private float unc;
	
	public int getTMAX() {
		return TMAX;
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

	public Stock(String sym, String nameCompany, int quantity, float uncertainty
			, long price, int TMAX) {
		this.symbol = sym;
		this.name = nameCompany;
		this.setStockQty(quantity);
		this.setUnc(uncertainty);
		this.stockPrice = price;
		this.volumeTraded = 0;
		this.lastTransactionPrice = price;
		this.TMAX = TMAX;
	}
	
	//Calculates new Stock price given volume of stock traded and trading price
	public void newPrice() {
		long tradePrice = getLastTransactionPrice();
		long price = getStockPrice();
		float sigmaS = getUnc();
		float temp;
		if(getVolumeTraded()!=0)
			temp = TMAX/getVolumeTraded() - 1;
		else
			temp = 1;
		float sigmaE = sigmaS*temp;
		float k = (sigmaS*sigmaS)/((sigmaS*sigmaS)+(sigmaE*sigmaE));
		price = (long) (price + (k*(tradePrice - price)));
		setStockPrice((long) price);
		float temp2 = 1 - (k*sigmaS);
		setUnc((float) Math.sqrt(temp2)); //New uncertainty
	}
	
	public long getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(long stockPrice) {
		this.stockPrice = stockPrice;
	}

	public float getUnc() {
		return unc;
	}

	public void setUnc(float unc) {
		this.unc = unc;
	}

	public long getVolumeTraded() {
		return volumeTraded;
	}

	public void addVolume(long volumeTraded) {
		this.volumeTraded += volumeTraded;
	}
}
