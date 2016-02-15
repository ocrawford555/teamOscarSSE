package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.List;

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
		setUnc((float) Math.sqrt(temp2)); 	//New uncertainty
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
		for (int i=0; i<=50; i++) {
			for (int j=pointAvgPtr; j>pointAvgPtr-i+1; j--) {
				//System.out.println("J: "+j);
				if (j>=0) priceA = priceBuf[j];
				else {priceA = priceBuf[j+50];}
				if ((j-1)>=0) priceB = priceBuf[(j-1)];
				else {priceB = priceBuf[(j-1)+50];}
				change += (priceA - priceB);
			}
			if (i == 0) rateOfChange.set(i, 0F);
			else rateOfChange.set(i, change/(i-1));
			priceA = 0;
			priceB = 0;
			change = 0;
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
	
	/*
	public static void main(String[] args) {
		Stock s = new Stock("BAML", "BoA", 2000, 0.2F, 120, 2000);
		Random r = new Random();	
//		System.out.println("---Before---");
//		s.printMetrics();
		for (int i=120; i<200; i++) {
			s.setLastTransactionPrice(r.nextInt(250));
			s.newPrice();
		}
		System.out.println("---After---");
		s.printMetrics();
		
		System.out.println("0th from list "+s.transactionAvg.get(0));
		System.out.println("5th from list "+s.rateOfChange.get(5));
		System.out.println("20th from list "+s.rateOfChange.get(20));
		System.out.println("50th from list "+s.rateOfChange.get(50));
	}
	*/
	
	
}
