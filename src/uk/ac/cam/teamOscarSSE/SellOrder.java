package uk.ac.cam.teamOscarSSE;

public class SellOrder extends Order implements Comparable<SellOrder> {
	public SellOrder(Stock s, Player p, int numShares, long price) {
		super(OrderType.SELL, p.getToken(), s, numShares, price);
	}

	@Override
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