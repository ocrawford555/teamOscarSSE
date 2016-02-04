package uk.ac.cam.teamOscarSSE;

public class BuyOrder extends Order implements Comparable<BuyOrder> {
	public BuyOrder(Stock s, Player p, int numShares, long price) {
		super(OrderType.BUY, p.getToken(), s, numShares, price);
	}

	@Override
	//sort by price (high to low), then time (earliest to latest)
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
