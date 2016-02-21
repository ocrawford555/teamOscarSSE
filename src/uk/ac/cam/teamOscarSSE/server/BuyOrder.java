package uk.ac.cam.teamOscarSSE.server;

public class BuyOrder extends Order implements Comparable<BuyOrder> {
	public BuyOrder(Stock s, Trader p, int numShares, long price) {
		super(OrderType.BUY, p.getToken(), s, numShares, price);
	}

	@Override
	/**
	 * Compare method makes sure that all buy orders
	 * that are added to the order book are in the correct
	 * order - i.e. the buys with the highest price are
	 * at the top of the order book, with ties broken
	 * on which order arrived first.
	 */
	public int compareTo(BuyOrder buyO) {
		if (this.getPrice() < buyO.getPrice()) return 1;
		else if (this.getPrice() > buyO.getPrice()) return -1;
		else {
			if (this.getTime() < buyO.getTime()) return 1;
			else if (this.getTime() > buyO.getTime()) return -1;
			else return 0;
		}
	}
}
