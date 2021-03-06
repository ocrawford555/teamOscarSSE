package uk.ac.cam.teamOscarSSE.testing;

import uk.ac.cam.teamOscarSSE.server.*;

import java.util.ArrayList;
import java.util.Random;

public class RandomTrading implements Runnable {
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange exchange;

	public RandomTrading(Exchange e, ArrayList<Stock> s, ArrayList<Player> p){
		stocks = s;
		players = p;
		exchange = e;
	}

	@Override
	public void run() {
		Random rand = new Random();
		
		while (exchange.isOpen()) {
			try {
				int nextWait = rand.nextInt(150) + 25;
				Thread.sleep(nextWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Stock s : stocks) {
				long delta = rand.nextInt(21) - 10;
				int amount = rand.nextInt(150) + 20;
				
				long pennyBuy = s.getBestBid();
				long pennySell = s.getBestOffer();
				
				//chooses player to make exchange more random
				//int playingFor = rand.nextInt(4);
				
				//add basically random order to the book
				exchange.addOrder(new BuyOrder(s,players.get(1),amount,pennyBuy+delta));
				exchange.addOrder(new SellOrder(s,players.get(1),amount,pennySell-delta));
			}
		}
	}
}
