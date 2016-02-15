package uk.ac.cam.teamOscarSSE;

public class ShortOrder extends Order implements Comparable<ShortOrder> {
	public ShortOrder(Stock s, Trader p, int numShares, long price) {
		super(OrderType.SHORT, p.getToken(), s, numShares, price);
	}

	@Override
	/**
	 * Compare method makes sure that all short orders
	 * that are added to the order book are in the correct
	 * order - i.e. the shorts with the highest price are
	 * at the top of the order book, with ties broken
	 * on which order arrived first.
	 */
	public int compareTo(ShortOrder sellO) {
		if (this.getPrice() > sellO.getPrice()) return 1;
		else if (this.getPrice() < sellO.getPrice()) return -1;
		else {
			if (this.getTime() < sellO.getTime()) return 1;
			else if (this.getTime() > sellO.getTime()) return -1;
			else return 0;
		}
	}
}