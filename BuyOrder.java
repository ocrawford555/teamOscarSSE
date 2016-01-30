package teamOscarSSE;

public class BuyOrder extends Order implements Comparable<BuyOrder> {
	public BuyOrder(Stock s, Player p, int numShares, double price){
		setTypeOfOrder(BUY);
		setStock(s);
		setId(p.getToken());
		setShares(numShares);
		setPrice(price);
		setOrderNum(IDGenerator.getID());
		setTime(System.currentTimeMillis());
	}

	@Override
	//sort by price (high to low), then time (earliest to latest)
	public int compareTo(BuyOrder buyO) {
		if(this.getPrice() < buyO.getPrice()) return 1;
		else if (this.getPrice() > buyO.getPrice()) return -1;
		else {
			if (this.getTime() < buyO.getTime()) return 1;
			else if (this.getTime() > buyO.getTime()) return -1;
			else return 0;
		}
	}
}
