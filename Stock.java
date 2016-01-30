package teamOscarSSE;

public class Stock {
	private String symbol;
	private String name;
	private double lastTransactionPrice;
	private int stockQty;
	private double bestBid;
	private double bestOffer;
	private double valueEstimate;
	
	public double getValueEstimate() {
		return valueEstimate;
	}
	public void setValueEstimate() {
		this.valueEstimate = (bestBid+bestOffer)/2;
	}
	public double getBestBid() {
		return bestBid;
	}
	public void setBestBid(double bestBid) {
		this.bestBid = bestBid;
	}
	public double getBestOffer() {
		return bestOffer;
	}
	public void setBestOffer(double bestOffer) {
		this.bestOffer = bestOffer;
	}
	public String getSymbol() {
		return symbol;
	}
	public String getName() {
		return name;
	}
	public double getLastTransactionPrice() {
		return lastTransactionPrice;
	}
	public void setLastTransactionPrice(double lastTransactionPrice) {
		this.lastTransactionPrice = lastTransactionPrice;
	}
	public int getStockQty() {
		return stockQty;
	}
	public void setStockQty(int amountToAdd) {
		this.stockQty+=amountToAdd;
	}
	public void addStockQty(int amountToAdd) {
		this.stockQty+=amountToAdd;
	}
	public void removeStockQty(int amountToRemove) {
		this.stockQty-=amountToRemove;
	}
	
	public Stock(String sym, String nameCompany, int quantity){
		this.symbol = sym;
		this.name = nameCompany;
		this.setStockQty(quantity);
	}
}
