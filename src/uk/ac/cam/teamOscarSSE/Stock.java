package uk.ac.cam.teamOscarSSE;

public class Stock {
	private String symbol;
	private String name;
	private long lastTransactionPrice;
	private int stockQty;
	private long bestBid;
	private long bestOffer;
	private long valueEstimate;

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

	public Stock(String sym, String nameCompany, int quantity) {
		this.symbol = sym;
		this.name = nameCompany;
		this.setStockQty(quantity);
	}
}
