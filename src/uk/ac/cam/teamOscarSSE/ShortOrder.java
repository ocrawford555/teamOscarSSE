package uk.ac.cam.teamOscarSSE;

public class ShortOrder extends Order implements Comparable<ShortOrder> {
	public ShortOrder(Stock s, Player p, int numShares, long price) {
		super(OrderType.SHORT, p.getToken(), s, numShares, price);
	}

	@Override
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