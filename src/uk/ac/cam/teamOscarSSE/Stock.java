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
		pointAvg5 = price; 
		pointAvg20 = price;
		pointAvg50 = price;
		overallAvg = price;
		cumulativePrices = 0;
		noOfTrades = 0;
		rateOfChange5 = 0;
		rateOfChange20 = 0;
		rateOfChange50 = 0;
		transactionAvg5 = 0;
		transactionAvg20 = 0;
		transactionAvg50 = 0;
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
	private long pointAvg5;
	private long pointAvg20;
	private long pointAvg50;
	
	//Overall price of stock since beginning of Exchange
	private long overallAvg;
	private long cumulativePrices;
	private long noOfTrades;

	//Moving point averages of transaction price
	private long[] transactionBuf = new long[50];
	private int transactionPtr = -1;
	private long transactionAvg5;
	private long transactionAvg20;
	private long transactionAvg50;

	//Change in stock price
	private float rateOfChange5;
	private float rateOfChange20;
	private float rateOfChange50;
	
	//Getter methods for user metrics
	public long getPointAvg5() {
		return pointAvg5;
	}

	public long getPointAvg20() {
		return pointAvg20;
	}

	public long getPointAvg50() {
		return pointAvg50;
	}

	public long overallAverage() {
		return overallAvg;
	}

	public long transactionAvg5() {
		return transactionAvg5;
	}
		
	public long transactionAvg20() {
		return transactionAvg20;
	}
		
	public long transactionAvg50() {
		return transactionAvg50;
	}

	public float rateOfChange5() {
		return rateOfChange5;
	}

	public float rateOfChange20() {
		return rateOfChange20;
	}

	public float rateOfChange50() {
		return rateOfChange50;
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
		long temp = 0;
		for (int i=pointAvgPtr; i>pointAvgPtr-5; i--) {
			if (i>=0) temp += priceBuf[i];
			else temp += priceBuf[i+50];
		}
		pointAvg5 = temp / 5;
		
		temp = 0;
		for (int i=pointAvgPtr; i>pointAvgPtr-20; i--) {
			if (i>=0) temp += priceBuf[i];
			else temp += priceBuf[i+50];
		}
		pointAvg20 = temp / 20;
		
		temp = 0;
		for (int i=0; i<50; i++) {
			temp += priceBuf[i];
		}
		pointAvg50 = temp / 50;
		
		//Set transactionAvg Metrics
		temp = 0;
		for (int i=transactionPtr; i>transactionPtr-5; i--) {
			if (i>=0) temp += transactionBuf[i];
			else temp += transactionBuf[i+50];
		}
		transactionAvg5 = temp / 5;
		
		temp = 0;
		for (int i=transactionPtr; i>transactionPtr-20; i--) {
			if (i>=0) temp += transactionBuf[i];
			else temp += transactionBuf[i+50];
		}
		transactionAvg20 = temp / 20;
		
		
		temp = 0;
		for (int i=transactionPtr; i>transactionPtr-50; i--) {
			if (i>=0) temp += transactionBuf[i];
			else temp += transactionBuf[i+50];
		}
		transactionAvg50 = temp / 50;
		
		
		//Set overallAvg Metrics
		overallAvg = cumulativePrices/noOfTrades;
		
		//Set rateOfChange Metrics
		long priceA = 0;
		long priceB = 0;
		float change = 0;
		for (int i=pointAvgPtr; i>pointAvgPtr-4; i--) {
			if (i>=0) {
				priceA = priceBuf[i];
				System.out.println("Pricea: "+priceA);
			}
			else {
				priceA = priceBuf[i+50];
				System.out.println("PriceA: "+priceA);
			}
			if (i-1>=0) {
				priceB = priceBuf[(i-1)];
				System.out.println("Priceb: "+priceB);
			}
			else {
				priceB = priceBuf[i-1+50];
				System.out.println("PriceB: "+priceB);
			}
			
			change += (priceA - priceB);
			System.out.println("Change: "+change);
		}
		rateOfChange5 = change/5;
		System.out.println("rateOfChange: "+rateOfChange5);
		
		priceA = 0;
		priceB = 0;
		change = 0;
		for (int i=pointAvgPtr; i>pointAvgPtr-19; i--) {
			if (i>=0) priceA = priceBuf[i];
			else {priceA = priceBuf[i+50];}
			if ((i+1)%50>=0) priceB = priceBuf[(i+1)%50];
			else {priceB = priceBuf[i+1+50];}
			
			change += (priceA - priceB);
		}
		rateOfChange20 = change/20;
		
		priceA = 0;
		priceB = 0;
		change = 0;
		for (int i=pointAvgPtr; i>pointAvgPtr-49; i--) {
			if (i>=0) priceA = priceBuf[i];
			else {priceA = priceBuf[i+50];}
			if ((i+1)%50>=0) priceB = priceBuf[(i+1)%50];
			else {priceB = priceBuf[i+1+50];}
			
			change += (priceA - priceB);
		}
		rateOfChange50 = change/50;
	}
	
	//printMetrics() is for debugging purposes, to see how metrics have changed
	private void printMetrics() {
		System.out.println("5-point-avg: "+pointAvg5);
		System.out.println("20-point-avg: "+pointAvg20);
		System.out.println("50-point-avg: "+pointAvg50);
		
		System.out.println("Overall-avg: "+overallAvg);
		System.out.println("Cumulative-prices: "+cumulativePrices);
		System.out.println("No-of-trades: "+noOfTrades);
		
		System.out.println("Transaction-Avg-5: "+transactionAvg5);
		System.out.println("Transaction-Avg-20: "+transactionAvg20);
		System.out.println("Transaction-Avg-50: "+transactionAvg50);
		
		System.out.println("Rate-Of-Change-5: "+rateOfChange5);
		System.out.println("Rate-Of-Change-20: "+rateOfChange20);
		System.out.println("Rate-Of-Change-50: "+rateOfChange50);
	}
	
	
}
