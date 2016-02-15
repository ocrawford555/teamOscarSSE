package uk.ac.cam.teamOscarSSE;

public class SellOrder extends Order implements Comparable<SellOrder> {
	public SellOrder(Stock s, Trader p, int numShares, long price) {
		super(OrderType.SELL, p.getToken(), s, numShares, price);
	}

	@Override
	/**
	 * Compare method makes sure that all sell orders
	 * that are added to the order book are in the correct
	 * order - i.e. the sells with the lowest price are
	 * at the top of the order book, with ties broken
	 * on which order arrived first.
	 */
	public int compareTo(SellOrder sellO) {
		if (this.getPrice() > sellO.getPrice()) return 1;
		else if (this.getPrice() < sellO.getPrice()) return -1;
		else {
			if (this.getTime() < sellO.getTime()) return 1;
			else if (this.getTime() > sellO.getTime()) return -1;
			else return 0;
		}
	}
}