package uk.ac.cam.teamOscarSSE;

public class BuyToCoverOrder extends Order implements Comparable<BuyToCoverOrder> {
	public BuyToCoverOrder(Stock s, Trader p, int numShares, long price) {
		super(OrderType.BUY_TO_COVER, p.getToken(), s, numShares, price);
	}

	@Override
	/**
	 * Compare method makes sure that all buy to cover orders
	 * that are added to the order book are in the correct
	 * order - i.e. the buys with the highest price are
	 * at the top of the order book, with ties broken
	 * on which order arrived first.
	 */
	public int compareTo(BuyToCoverOrder buyO) {
		if (this.getPrice() < buyO.getPrice()) return 1;
		else if (this.getPrice() > buyO.getPrice()) return -1;
		else {
			if (this.getTime() < buyO.getTime()) return 1;
			else if (this.getTime() > buyO.getTime()) return -1;
			else return 0;
		}
	}
}
