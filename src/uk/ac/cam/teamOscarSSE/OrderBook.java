package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderBook {
	private Stock s;

	public Stock getStock() {
		return s;
	}

	final List<BuyOrder> buys = new ArrayList<BuyOrder>();
	final List<SellOrder> sells = new ArrayList<SellOrder>();

	public OrderBook(Stock s) {
		this.s = s;
	}

	public void addOrder(BuyOrder o) {
		//BUY ORDER
		synchronized (buys) {
			buys.add(o);
			Collections.sort(buys);
			s.setBestBid(buys.get(0).getPrice());
		}
	}

	public void addOrder(SellOrder o) {
		//SELL ORDER
		synchronized (sells) {
			sells.add(o);
			Collections.sort(sells);
			s.setBestOffer(sells.get(0).getPrice());
		}
	}

	public synchronized boolean removeOrder(BuyOrder o) {
		synchronized (buys) {
			boolean removed = buys.remove(o);
			//arguably, only order removed is from head, so call below
			//not required
			Collections.sort(buys);

			// TODO: I put this check in to avoid exceptions. What's intended if the list is empty?
			if (!buys.isEmpty()) {
				s.setBestBid(buys.get(0).getPrice());
			}
			return removed;
		}
	}

	public synchronized boolean removeOrder(SellOrder o) {
		synchronized (sells) {
			boolean removed = sells.remove(o);
			//arguably, only order removed is from head, so call below
			//not required
			Collections.sort(sells);
			// TODO: I put this check in to avoid exceptions. What's intended if the list is empty?
			if (!sells.isEmpty()) {
				s.setBestOffer(sells.get(0).getPrice());
			}
			return removed;
		}
	}

	public void printPendingOrders(OrderType type) {
		//BUY
		//for showing only top five results
		int countBuys = 5;
		int countSells = 5;
		if (type == OrderType.BUY)
			synchronized (buys) {
				for (Order o : buys) {
					if (countBuys != 0) {
						System.out.println(o.getOrderNum() + " --- " + o.getId() + " --- " + o.getTime() + " --- " + o);
						countBuys--;
					} else break;
				}
			}
			//SELL
		else
			synchronized (sells) {
				for (Order o : sells) {
					if (countSells != 0) {
						System.out.println(o.getOrderNum() + " --- " + o.getId() + " --- " + o.getTime() + " --- " + o);
						countSells--;
					} else break;
				}
			}
	}

}