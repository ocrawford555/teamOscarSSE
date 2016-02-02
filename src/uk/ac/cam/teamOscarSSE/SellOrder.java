package uk.ac.cam.teamOscarSSE;

public class SellOrder extends Order implements Comparable<SellOrder> {
	public SellOrder(Stock s, Player p, int numShares, double price){
		setTypeOfOrder(SELL);
		setStock(s);
		setId(p.getToken());
		setShares(numShares);
		setPrice(price);
		setOrderNum(IDGenerator.getID());
		setTime(System.currentTimeMillis());
	}

	@Override
	public int compareTo(SellOrder sellO) {
		if(this.getPrice() > sellO.getPrice()) return 1;
		else if (this.getPrice() < sellO.getPrice()) return -1;
		else {
			if (this.getTime() < sellO.getTime()) return 1;
			else if (this.getTime() > sellO.getTime()) return -1;
			else return 0;
		}
	}
}