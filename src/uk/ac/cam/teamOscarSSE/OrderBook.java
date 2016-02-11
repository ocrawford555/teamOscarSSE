package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderBook {
	final List<BuyOrder> buys = new ArrayList<>();
	final List<SellOrder> sells = new ArrayList<>();
	private Stock s;
	public OrderBook(Stock s) {
		this.s = s;
	}

	public Stock getStock() {
		return s;
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

	public void printPendingOrders(OrderType type, int maxNum) {
		//BUY
		//for showing only top five results
		int ctr = maxNum;
		if (type == OrderType.BUY)
			synchronized (buys) {
				for (Order o : buys) {
					if (ctr != 0) {
						System.out.println(o.getOrderNum() + " --- " + o.getId() + " --- " + o.getTime() + " --- " + o);
						ctr--;
					} else break;
				}
			}
			//SELL
		else
			synchronized (sells) {
				for (Order o : sells) {
					if (ctr != 0) {
						System.out.println(o.getOrderNum() + " --- " + o.getId() + " --- " + o.getTime() + " --- " + o);
						ctr--;
					} else break;
				}
			}
	}

}