package uk.ac.cam.teamOscarSSE.server;

import java.util.ArrayList;
import java.util.List;

public class Stock {
	long[] tempRateChange = new long[49];
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
	//Variables used to provide users with metrics
	//Moving point averages of stock price
	private long[] priceBuf = new long[50];
	private int pointAvgPtr = -1;
	private List<Long> pointAvg = new ArrayList<Long>();
	//Overall price of stock since beginning of Exchange
	private long overallAvg;
	private long cumulativePrices;
	private long noOfTrades;
	//Moving point averages of transaction price
	private long[] transactionBuf = new long[50];
	private int transactionPtr = -1;
	private List<Long> transactionAvg = new ArrayList<Long>();
	//Change in stock price, so e.g. rateOfChange.get(20) returns the average of rate of change between 20 stock prices, i.e. aveage of 19 rates
	private List<Float> rateOfChange = new ArrayList<Float>();

	public Stock(String sym, String nameCompany, int quantity, float uncertainty, long price, int TMAX) {
		this.symbol = sym;
		this.name = nameCompany;
		this.setStockQty(quantity);
		this.setUnc(uncertainty);
		this.stockPrice = price;
		this.volumeTraded = 0;
		this.lastTransactionPrice = price;
		this.TMAX = TMAX;
		
		//Initialise pointAvgBuf and other metrics
		for (int i=0; i<50; i++) {
			priceBuf[i] = price;
			transactionBuf[i] = price;
		}
		overallAvg = price;
		cumulativePrices = 0;
		noOfTrades = 0;
		for (int i=0; i<=50; i++) {
			pointAvg.add(price);
			transactionAvg.add(price);
			rateOfChange.add(0F);
		}
	}

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

	//Calculates new Stock price given volume of stock traded and trading price
	public void newPrice() {
		long tradePrice = getLastTransactionPrice();
		long price = getStockPrice();
		float sigmaS = getUnc();
		float temp;
		if (getVolumeTraded() != 0)
			temp = TMAX / getVolumeTraded() - 1;
		else
			temp = 1;
		float sigmaE = sigmaS * temp;
		float k = (sigmaS * sigmaS) / ((sigmaS * sigmaS) + (sigmaE * sigmaE));
		price = (long) (price + (k * (tradePrice - price)));
		setStockPrice((long) price);
		float temp2 = 1 - (k * sigmaS);
		setUnc((float) Math.sqrt(temp2));    //New uncertainty
		updateMetrics();
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
	
	//Getter methods for user metrics
	public List<Long> getPointAvg() {
		return pointAvg;
	}

	public long getOverallAverage() {
		return overallAvg;
	}

	public List<Long> getTransactionAvg() {
		return transactionAvg;
	}

	public List<Float> getRateOfChange() {
		return rateOfChange;
	}
	//End of getter methods
	
	private void updateMetrics() {
		//The pointers point at the last index at which data was written to
		pointAvgPtr = (pointAvgPtr+1) % 50;
		priceBuf[pointAvgPtr] = stockPrice;
		
		cumulativePrices += stockPrice;
		noOfTrades++;
		
		transactionPtr = (transactionPtr+1) % 50;
		transactionBuf[transactionPtr] = lastTransactionPrice;
		
		calcAndSetMetrics();
	}
	
	private void calcAndSetMetrics() {
		//Set pointAvg Metrics
			
		/* List implementation of moving averages */
		long temp = 0;
		for (int i=0; i<=50; i++) {
			for (int j=pointAvgPtr; j>pointAvgPtr-i; j--) {
				if (j>=0) temp += priceBuf[j];
				else temp += priceBuf[j+50];
			}
			if (i == 0) pointAvg.set(i, stockPrice);
			else pointAvg.set(i, temp/i);
			temp = 0;
		}
		
				
		/* List implementation of transactional averages */
		temp = 0;
		for (int i=0; i<=50; i++) {
			for (int j=transactionPtr; j>transactionPtr-i; j--) {
				if (j>=0) temp += transactionBuf[j];
				else temp += transactionBuf[j+50];
			}
			if (i == 0) transactionAvg.set(i, lastTransactionPrice);
			else transactionAvg.set(i, temp/i);
			temp = 0;
		}
		
		//Set overallAvg Metrics
		overallAvg = cumulativePrices/noOfTrades;
		
			
		/* List implementation of rate of change */
		long priceA = 0;
		long priceB = 0;
		float change = 0;
		//tempRateChange holds price differences
		tempRateChange = new long[49];
		for (int i=0; i<priceBuf.length-1; i++) {
			priceA = priceBuf[i];
			priceB = priceBuf[i+1];
			tempRateChange[i] = priceB - priceA;
		}
		int count = 2;
		for (int i=pointAvgPtr-1; i>pointAvgPtr-50; i--) {
			int j = i;
			if (i<0) j+=49;
			change += tempRateChange[j];
			rateOfChange.set(count, change/(count-1));
			count++;
		}
		
	}
	
	//printMetrics() is for debugging purposes, to see how metrics have changed
	private void printMetrics() {
		System.out.println("Stock-price: "+stockPrice);
		
		System.out.println("5-point-avg: "+pointAvg.get(5));
		System.out.println("20-point-avg: "+pointAvg.get(20));
		System.out.println("50-point-avg: "+pointAvg.get(50));
		
		System.out.println("Overall-avg: "+overallAvg);
		System.out.println("Cumulative-prices: "+cumulativePrices);
		System.out.println("No-of-trades: "+noOfTrades);
		
		System.out.println("Transaction-Avg-5: "+transactionAvg.get(5));
		System.out.println("Transaction-Avg-20: "+transactionAvg.get(20));
		System.out.println("Transaction-Avg-50: "+transactionAvg.get(50));
		
		System.out.println("Rate-Of-Change-5: "+rateOfChange.get(5));
		System.out.println("Rate-Of-Change-20: "+rateOfChange.get(20));
		System.out.println("Rate-Of-Change-50: "+rateOfChange.get(50));
	}
}
