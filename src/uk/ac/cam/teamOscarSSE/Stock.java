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
		float temp = 1;
//		if(getVolumeTraded()!=0)
//			temp = TMAX/getVolumeTraded() - 1;
//		else
//			temp = 1;
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

/*
	//Variables that will be used to provide users with metrics
	private static long pointAvg5;
	private static long pointAvg20;
	private static long pointAvg50;
	private static long overallAverage;
	private static long transactionChange;
	private static float rateOfChange5;
	private static float rateOfChange20;
	private static float rateOfChange50;

	//Getter methods for user metrics
	public static long getPointAvg6() {
		return pointAvg5;
	}

	public static long getPointAvg20() {
		return pointAvg20;
	}

	public static long getPointAvg50() {
		return pointAvg50;
	}

	public static long overallAverage() {
		return overallAverage;
	}

	public static long transactionChange() {
		return transactionChange;
	}

	public static float rateOfChange5() {
		return rateOfChange5;
	}

	public static float rateOfChange20() {
		return rateOfChange20;
	}

	public static float rateOfChange50() {
		return rateOfChange50;
	}
	//End of getter methods
	*/
}
