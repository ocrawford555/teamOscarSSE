package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.Random;

public class MainAlgoTest {
	//have these as static -- only need one copy
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange2 exchange;
	static LeaderBoard lb;

	public static void testExchange() {
		Pennying p = new Pennying(exchange,stocks,players,lb);
		Thread a = new Thread(p);
		Randy d = new Randy(exchange,stocks,players);
		Thread b = new Thread(d);
		Thread c = new Thread(d);
		
		Player mmP = new Player("MARKET_MAKER", "");
		exchange.addPlayer(mmP);
		Player gbP = new Player("GEN_BOT","");
		exchange.addPlayer(gbP);
		Player bbP = new Player("BOOM_BOT","");
		exchange.addPlayer(bbP);
		Player rbP = new Player("RECESSION_BOT","");
		exchange.addPlayer(rbP);
		
		MarketMaker mm = new MarketMaker(exchange, stocks.get(0), 10, 10, 15, mmP);
		GeneralBot gb = new GeneralBot(exchange, stocks.get(0),gbP);
		BoomBot bb = new BoomBot(exchange, stocks.get(0),bbP);
		RecessionBot rb = new RecessionBot(exchange,stocks.get(0),rbP);
		
		Thread f = new Thread(mm);
		Thread g = new Thread(gb);
		Thread h = new Thread(bb);
		Thread j = new Thread(rb);
		
		System.out.println("Exchange running.");
		
		a.start();
		b.start();
		c.start();
		f.start();
		g.start();
		h.start();
		j.start();
		
		try {
			//let game run for 20 seconds
			Thread.sleep(20000);
			System.out.println("Exchange ended. Market closed.");
			//update the leaderboard
			System.out.println("");
			System.out.println("Final Portfolio Contents");
			for(Player px:players) {
				System.out.println(px.getName() + " "); px.getPortfoio().contents();
			}
			System.out.println("");
			for(Player px:players) {
				System.out.println(px.getName() + " -- " + px.getBalance());
			}
		
			lb.update();
			
			//close the exchange
			exchange.setClosed(true);
			System.out.println("");
			System.out.println("");
			System.out.println("--- ROUND OVER ---");
			lb.get();
//			System.out.println("MMA: " + (mmP.getBalance()-10000000));
//			System.out.println("GEN: " + (gbP.getBalance()-10000000));
//			System.out.println("BBT: " + (bbP.getBalance()-10000000));
//			System.out.println("RBT: " + (rbP.getBalance()-10000000));
			System.out.println("");
			System.out.println("");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void open() {
		//create and add stocks
		Stock stock1 = new Stock("BP", "British Petroleum", 5000,0.2f,34660);
		stocks.add(stock1);
//		Stock stock2 = new Stock("BAM", "Bank of America", 12000,0.2f,1254);
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
		lb.get();

		//create the exchange
		exchange = new Exchange2(stocks);

		//add the players to the exchange
		for (Player player : players) {
			exchange.addPlayer(player);
		}

		//add some orders to the order book to initiate trading
		exchange.addOrder(new BuyOrder(stock1, Alice, 20, 34638));
		exchange.addOrder(new BuyOrder(stock1, Oliver, 20, 34640));
		exchange.addOrder(new BuyOrder(stock1, Alice, 20, 34642));
//		exchange.addOrder(new BuyOrder(stock2, Oliver, 90, 1287));
//		exchange.addOrder(new BuyOrder(stock2, Alice, 40, 1290));

		exchange.addOrder(new SellOrder(stock1, Oliver, 25, 34690));
		exchange.addOrder(new SellOrder(stock1, Alice, 25, 34690));
		exchange.addOrder(new SellOrder(stock1, Oliver, 25, 34692));
//		exchange.addOrder(new SellOrder(stock2, Alice, 27, 1297));
//		exchange.addOrder(new SellOrder(stock2, Oliver, 30, 1298));
		
		//exchange.printOrderBooks();
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
				//execute something every 0.25 seconds
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			for (Stock s : stocks) {
				long pennyBuy = s.getBestBid();
				long pennySell = s.getBestOffer();
				int amount = rand.nextInt(25) + 5;
				
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
				//execute something every second
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Stock s : stocks) {
				long delta = rand.nextInt(5);
				int amount = rand.nextInt(25) + 8;
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
			
			exchange.printOrderBooks();
			System.out.println("Value of BP from algorithm: " + stocks.get(0).getStockPrice());
		}
	}
}