package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderBook {
	private Stock s;
	public Stock getS() {
		return s;
	}

	List<BuyOrder> buys = new ArrayList<BuyOrder>();
	List<SellOrder> sells = new ArrayList<SellOrder>();

	public OrderBook(Stock s){
		this.s = s;
	}

	public void addOrder(BuyOrder o){
		//BUY ORDER
		buys.add(o);
		Collections.sort(buys);
	}
	
	public void addOrder(SellOrder o){
		//BUY ORDER
		sells.add(o);
		Collections.sort(sells);
	}
	
	public void removeOrder(BuyOrder o){
		buys.remove(o);
		//arguably, only order removed is from head, so call below
		//not required
		Collections.sort(buys);
	}
	
	public void removeOrder(SellOrder o){
		sells.remove(o);
		//arguably, only order removed is from head, so call below
		//not required
		Collections.sort(sells);
	}

	public void printPendingOrders(int type) {
		//BUY
		if(type == 1)
			for(Order o: buys){
				System.out.println(o.getOrderNum() + " --- " + o.getId() + " --- " + o.getTime());
			}
		//SELL
		else 
			for(Order o: sells){
				System.out.println(o.getOrderNum());
			}
	}

}
