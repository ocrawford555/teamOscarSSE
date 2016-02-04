package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Random;

public class MainAlgoTest {
	//have these as static -- only need one copy
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange2 exchange;
	static LeaderBoard lb;

	//TODO: message to pass to web client?
	public static void onOrderChange(OrderChangeMessage msg) {}

	public static void testExchange() {
		//start some algos and pass them parameters
		//running as threads so concurrent access needs to 
		//be handled
		Pennying p = new Pennying(exchange,stocks,players,lb);
		Thread a = new Thread(p);
		Randy d = new Randy(exchange,stocks,players);
		Thread b = new Thread(d);
		Thread c = new Thread(d);

		a.start();
		b.start();
		c.start();
		
		try {
			Thread.sleep(30000);
			exchange.setClosed(true);
			System.out.println("");
			System.out.println("");
			System.out.println("--- ROUND OVER ---");
			lb.get();
			System.out.println("");
			System.out.println("");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void open() {
		//create and add stocks
		Stock stock1 = new Stock("BP", "British Petroleum", 5000,0);
		stocks.add(stock1);
		Stock stock2 = new Stock("BAM", "Bank of America", 12000,0);
		stocks.add(stock2);

		//create and add players to the game
		Player Oliver = new Player("Oliver", "o.crawford@hotmail.co.uk");
		players.add(Oliver);
		Player Alice = new Player("Alice", "art35@cam.ac.uk");
		players.add(Alice);
		Player Bob = new Player("Bob", "legend@awesome.com");
		players.add(Bob);
		Player Kate = new Player("Kate", "kate@me.com");
		players.add(Kate);

		//create the leader board
		lb = new LeaderBoard(players);
		lb.get();

		//create the exchange
		exchange = new Exchange2(stocks);

		//add the players to the exchange
		//TODO: add bots to the exchange as players, or just let them run
		//with no tracking information?
		for (Player player : players) {
			exchange.addPlayer(player);
		}

		//add some orders to the order book to initiate trading
		exchange.addOrder(new BuyOrder(stock1, Alice, 100, 34635));
		exchange.addOrder(new BuyOrder(stock1, Oliver, 150, 34678));
		exchange.addOrder(new BuyOrder(stock1, Alice, 100, 34655));
		exchange.addOrder(new BuyOrder(stock2, Oliver, 270, 1287));
		exchange.addOrder(new BuyOrder(stock2, Alice, 100, 1290));

		exchange.addOrder(new SellOrder(stock1, Oliver, 200, 34690));
		exchange.addOrder(new SellOrder(stock1, Alice, 560, 34690));
		exchange.addOrder(new SellOrder(stock1, Oliver, 70, 34695));
		exchange.addOrder(new SellOrder(stock2, Alice, 270, 1300));
		exchange.addOrder(new SellOrder(stock2, Oliver, 100, 1301));
		
		exchange.printOrderBooks();
	}

	public static void main(String args[]) {
		open();
		testExchange();
	}
}

class Pennying implements Runnable{
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange2 exchange;
	static LeaderBoard lb;

	public Pennying(Exchange2 e, ArrayList<Stock> s, ArrayList<Player> p, LeaderBoard l){
		stocks = s;
		players = p;
		exchange = e;
		lb = l;
	}

	@Override
	public void run() {
		Random rand = new Random();
		
		while (true && !exchange.isClosed()) {
			try {
				//execute something every 0.40 seconds
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			exchange.printOrderBooks();
			lb.get();
			
			for (Stock s : stocks) {
				long pennyBuy = s.getBestBid();
				long pennySell = s.getBestOffer();
				int amount = rand.nextInt(40) + 8;
				
				//chooses player to make exchange more random
				int playingFor = rand.nextInt(4);
				
				//initiate pennying
				exchange.addOrder(new BuyOrder(s,players.get(playingFor),amount,pennyBuy+10));
				exchange.addOrder(new SellOrder(s,players.get(playingFor),amount,pennySell-10));
			}		
			lb.update();
		}
	}

}

class Randy implements Runnable{
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange2 exchange;

	public Randy(Exchange2 e, ArrayList<Stock> s, ArrayList<Player> p){
		stocks = s;
		players = p;
		exchange = e;
	}

	@Override
	public void run() {
		Random rand = new Random();
		
		while (true && !exchange.isClosed()) {
			try {
				//execute something every 0.30 seconds
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Stock s : stocks) {
				long delta = rand.nextInt(30);
				int amount = rand.nextInt(40) + 8;
				int direction = Math.round((float)Math.random() + 1);
				if(direction==1) delta*=-1;
				long pennyBuy = s.getBestBid();
				long pennySell = s.getBestOffer();
				
				//chooses player to make exchange more random
				int playingFor = rand.nextInt(4);
				
				//add basically random order to the book
				exchange.addOrder(new BuyOrder(s,players.get(playingFor),amount,pennyBuy+delta));
				exchange.addOrder(new SellOrder(s,players.get(playingFor),amount,pennySell-delta));
			}
		}
	}
}