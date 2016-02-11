package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Random;

public class PennyingAlgo implements Runnable{
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange exchange;

	public PennyingAlgo(Exchange e, ArrayList<Stock> s, ArrayList<Player> p){
		stocks = s;
		players = p;
		exchange = e;
	}

	@Override
	public void run() {
		Random rand = new Random();
		
		while (exchange.isOpen()) {
			try {
				int nextWait = rand.nextInt(200) + 25;
				Thread.sleep(nextWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			for (Stock s : stocks) {
				long pennyBuy = s.getBestBid();
				long pennySell = s.getBestOffer();
				int amount = rand.nextInt(350) + 5;
				
				//chooses player to make exchange more random
				//int playingFor = rand.nextInt(2);
				
				// Periodically remove all pending orders to unblock stock/cash.
				// Feel free to comment this out.
				if (rand.nextDouble() < 0.1) {
					exchange.removeAllOrders(players.get(0).getToken());
				}
				
				//initiate pennying
				exchange.addOrder(new BuyOrder(s,players.get(0),amount,pennyBuy+1));
				exchange.addOrder(new SellOrder(s,players.get(0),amount,pennySell-1));
			}		
		}
	}
}
