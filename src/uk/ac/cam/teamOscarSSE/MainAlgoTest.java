package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Random;

public class MainAlgoTest {
	//have these as static -- only need one copy
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange exchange;
	static LeaderBoard lb;

	public static void testExchange() {
		Pennying p = new Pennying(exchange,stocks,players,lb);
		Thread a = new Thread(p);
		Randy d = new Randy(exchange,stocks,players);
		Thread b = new Thread(p);
		Thread c = new Thread(d);
		
		MarketMaker mm = new MarketMaker(exchange, stocks.get(0),150,150,600);
		GeneralBot gb = new GeneralBot(exchange, stocks.get(0));
		BoomBot bb = new BoomBot(exchange, stocks.get(0));
		RecessionBot rb = new RecessionBot(exchange,stocks.get(0));

		Thread f = new Thread(mm);
		//Thread g = new Thread(gb);
		Thread h = new Thread(bb);
		//Thread j = new Thread(rb);
		
		a.start();
		b.start();
		c.start();
		
		f.start();
		//g.start();
		h.start();
		//j.start();
				
		try {
			Thread.sleep(7000);
			exchange.setOpen(false);

			lb.update();

			System.out.println("");
			System.out.println("");
			System.out.println("--- ROUND OVER ---");
			System.out.println("");
			System.out.println("");
			System.out.println("Final Portfolio Contents");
			for(Player px:players) {
				System.out.println(px.getName() + " ");
				px.getPortfolio().contents();
			}
			System.out.println("");
			System.out.println("");
			for(Player px:players) {
				System.out.println(px.getName() + " -- " + px.getBalance());
			}
			System.out.println("");
			System.out.println("");
			lb.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void open() {
		//create and add stocks
		Stock stock1 = new Stock("BP", "British Petroleum", 5000,0.2f,12347,1000);
		stocks.add(stock1);
//		Stock stock2 = new Stock("BAM", "Bank of America", 12000,0.2f,26487,125);
//		stocks.add(stock2);

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

		//create the exchange
		exchange = new Exchange(stocks);

		//add the players to the exchange
		for (Player player : players) {
			exchange.addPlayer(player);
		}

		// Open the exchange
		exchange.setOpen(true);

		//add some orders to the order book to initiate trading
		exchange.addOrder(new BuyOrder(stock1, Alice, 50, 12344));
		exchange.addOrder(new BuyOrder(stock1, Oliver, 75, 12345));
		exchange.addOrder(new BuyOrder(stock1, Bob, 50, 12342));
//		exchange.addOrder(new BuyOrder(stock2, Bob, 80, 26484));
//		exchange.addOrder(new BuyOrder(stock2, Alice, 40, 26486));

		exchange.addOrder(new SellOrder(stock1, Oliver, 20, 12348));
		exchange.addOrder(new SellOrder(stock1, Kate, 56, 12349));
		exchange.addOrder(new SellOrder(stock1, Oliver, 35, 12354));
//		exchange.addOrder(new SellOrder(stock2, Alice, 60, 26492));
//		exchange.addOrder(new SellOrder(stock2, Kate, 50, 26491));
	}

	public static void main(String args[]) {
		open();
		testExchange();
	}
}

class Pennying implements Runnable{
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange exchange;
	static LeaderBoard lb;

	public Pennying(Exchange e, ArrayList<Stock> s, ArrayList<Player> p, LeaderBoard l){
		stocks = s;
		players = p;
		exchange = e;
		lb = l;
	}

	@Override
	public void run() {
		Random rand = new Random();
		
		while (exchange.isOpen()) {
			try {
				int nextWait = rand.nextInt(300) + 75;
				Thread.sleep(nextWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			for (Stock s : stocks) {
				long pennyBuy = s.getBestBid();
				long pennySell = s.getBestOffer();
				int amount = rand.nextInt(400) + 200;
				
				//chooses player to make exchange more random
				int playingFor = rand.nextInt(4);
				
				//initiate pennying
				exchange.addOrder(new BuyOrder(s,players.get(playingFor),amount,pennyBuy+1));
				exchange.addOrder(new SellOrder(s,players.get(playingFor),amount,pennySell-1));
			}		
		}
	}

}

class Randy implements Runnable{
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange exchange;

	public Randy(Exchange e, ArrayList<Stock> s, ArrayList<Player> p){
		stocks = s;
		players = p;
		exchange = e;
	}

	@Override
	public void run() {
		Random rand = new Random();
		
		while (exchange.isOpen()) {
			try {
				//execute something every 0.21 seconds
				int nextWait = rand.nextInt(300) + 75;
				Thread.sleep(nextWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Stock s : stocks) {
				long delta = rand.nextInt(20);
				int amount = rand.nextInt(400) + 200;
				int direction = Math.round((float)Math.random() + 1f);
				if(direction==1) delta*=-1;
				long pennyBuy = s.getBestBid();
				long pennySell = s.getBestOffer();
				
				//chooses player to make exchange more random
				int playingFor = rand.nextInt(4);
				
				//add basically random order to the book
				exchange.addOrder(new BuyOrder(s,players.get(playingFor),amount,pennyBuy+delta));
				exchange.addOrder(new SellOrder(s,players.get(playingFor),amount,pennySell-delta));
			}
			
			//exchange.printOrderBooks();
			System.out.println("Value of BP from algorithm: " + stocks.get(0).getStockPrice());
			//System.out.println("Value of BAML from algorithm: " + stocks.get(1).getStockPrice());
		}
	}
}