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

	public OrderBook(Stock s) {
		this.s = s;
	}

	public void addOrder(BuyOrder o) {
		//BUY ORDER
		synchronized(buys){
			buys.add(o);
			Collections.sort(buys);
			s.setBestBid(buys.get(0).getPrice());
		}
	}

	public void addOrder(SellOrder o) {
		//SELL ORDER
		synchronized(sells){
			sells.add(o);
			Collections.sort(sells);
			s.setBestOffer(sells.get(0).getPrice());
		}
	}

	public synchronized void removeOrder(BuyOrder o) {
		synchronized(buys){
			buys.remove(o);
			//arguably, only order removed is from head, so call below
			//not required
			Collections.sort(buys);
			s.setBestBid(buys.get(0).getPrice());
		}
	}

	public synchronized void removeOrder(SellOrder o) {
		synchronized(sells){
			sells.remove(o);
			//arguably, only order removed is from head, so call below
			//not required
			Collections.sort(sells);
			s.setBestOffer(sells.get(0).getPrice());
		}
	}

	public void printPendingOrders(OrderType type) {
		//BUY
		//for showing only top three results
		int countBuys = 3;
		int countSells = 3;
		if (type == OrderType.BUY)
			synchronized(buys){
				for (Order o : buys) {
					if(countBuys != 0) {
						System.out.println(o.getOrderNum() + " --- " + o.getId() + " --- " + o.getTime() + " --- " + o);
						countBuys--;
					}
					else break;
				}
			}
		//SELL
		else
			synchronized(sells){
				for (Order o : sells) {
					if(countSells != 0) {
						System.out.println(o.getOrderNum() + " --- " + o.getId() + " --- " + o.getTime() + " --- " + o);
						countSells--;
					}
					else break;
				}
			}
	}

}
