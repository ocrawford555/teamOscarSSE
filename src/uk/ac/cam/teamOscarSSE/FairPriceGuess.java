package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Random;

/**
 * Fair Price guess.
 * 
 * Idea/Theory:
 * 
 * Suppose the fair price of the stock is halfway
 * between the bid and ask price. If the stock price
 * is above this fair value, then buy stocks, otherwise
 * sell them.
 */

public class FairPriceGuess implements Runnable {
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange exchange;
	private int flip = 0;

	public FairPriceGuess(Exchange e, ArrayList<Stock> s, ArrayList<Player> p){
		stocks = s;
		players = p;
		exchange = e;
		flip=0;
	}

	@Override
	public void run() {
		Random rand = new Random();

		while (exchange.isOpen()) {
			try {
				int nextWait = rand.nextInt(350) + 25;
				Thread.sleep(nextWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (Stock s : stocks) {
				long highBuy = s.getBestBid();
				long lowSell = s.getBestOffer();
				long middle = (highBuy + lowSell) / 2;
				

				//buy if current buy price is lower than VWAP score
				if(middle < s.getStockPrice()){
					int amount = rand.nextInt(50) + 10;
					exchange.addOrder(new BuyOrder(s,players.get(2),amount,highBuy+(2*(1+flip))));
				}

				if(middle > s.getStockPrice()){
					int amount = rand.nextInt(50) + 10;
					exchange.addOrder(new SellOrder(s,players.get(2),amount,lowSell-(2*(1+flip))));
				}

				// Periodically remove all pending orders to unblock stock/cash.
				// Feel free to comment this out.
				if (rand.nextDouble() < 0.1) {
					exchange.removeAllOrders(players.get(2+flip).getToken());
				}
				
				//flip = (flip==1) ? 0 : 1;
			}		
		}
		return;
	}
}