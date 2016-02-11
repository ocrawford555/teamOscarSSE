package uk.ac.cam.teamOscarSSE;

public class BuyToCoverOrder extends Order implements Comparable<BuyToCoverOrder> {
	public BuyToCoverOrder(Stock s, Trader p, int numShares, long price) {
		super(OrderType.BUY_TO_COVER, p.getToken(), s, numShares, price);
	}

	@Override
	//sort by price (high to low), then time (earliest to latest)
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
