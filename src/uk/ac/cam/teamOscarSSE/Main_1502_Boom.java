package uk.ac.cam.teamOscarSSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main_1502_Boom {
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static Exchange exchange;
	static LeaderBoard lb;
	static public List<Long> prices = new LinkedList<Long>();
	static public List<Long> balA = new LinkedList<Long>();
	static public List<Long> balB = new LinkedList<Long>();

	// The number of simulation steps.
	private static int NUM_SIM_STEPS = 600;

	public static void open() {
		//create and add stocks
		Stock stock1 = new Stock("BAML", "Bank of America", 5000,0.15f,3482,400);
		stocks.add(stock1);


		//create players
		//Alice will be using the pennying algorithm, and should win
		//over Bob.
		Player Alice = new Player("Alice", "A");
		players.add(Alice);


		//Bob is using the random order algorithm, which has no logic
		//Making a decent profit would be lucky, but is possible.
		Player Bob = new Player("Bob", "B");
		players.add(Bob);
		
		Player Cath = new Player("Cath", "C");
		players.add(Cath);
		
		Player Dan = new Player("Dan", "C");
		players.add(Dan);

		//create the leader board
		lb = new LeaderBoard(players);

		//create the exchange
		exchange = new Exchange(stocks);

		// TODO: temporary modification
		try {
			NewServer.start(8080, exchange);
		} catch (IOException e) {
			System.out.println("Failed to start the server.");
			e.printStackTrace();
			return;
		}
	
		UserFrameServer user = new UserFrameServer(stocks.get(0));
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//add the players to the exchange
		for (Player player : players) {
			exchange.addPlayer(player);
		}

		// Open the exchange
		exchange.setOpen(true);
		
		Thread haha = new Thread(user);
		haha.start();
	}

	public static void testExchange() {
		//create fake two user algorithms
		//both algorithms in theory could be ported to provide framework
		//for the eventual user of the competition
		PennyingAlgo penny = new PennyingAlgo(exchange,stocks,players);
		Thread user1 = new Thread(penny);
		Thread userb = new Thread(penny);
		RandomTrading random = new RandomTrading(exchange,stocks,players);
		Thread user2 = new Thread(random);
		FairPriceGuess fpg = new FairPriceGuess(exchange,stocks,players);
		Thread user3 = new Thread(fpg);
	

		//include a non-aggressive market maker to just set up some orders, and then
		//add some orders to the order book occasionally - not in the game for
		//profit -> this bot is simulating normal consumers looking to buy and 
		//sell stocks.
		MarketMaker mm = new MarketMaker(exchange, stocks.get(0),50,50,200);

		//general bot in play for simplification only
		GeneralBot gb = new GeneralBot(exchange, stocks.get(0));

		//price moving bot
		PriceMovingBot pmb = new PriceMovingBot(exchange,stocks.get(0));

		//boom bot
		BoomBot bb = new BoomBot(exchange,stocks.get(0));

		Thread marketM = new Thread(mm);
		Thread generalBot = new Thread(gb);
		Thread priceMover = new Thread(pmb);
		Thread boomBot = new Thread(bb);

		//start the trading
		user1.start();
		user2.start();
		user3.start();
		userb.start();
		marketM.start();
		//generalBot.start();
		priceMover.start();
		boomBot.start();

		for(int j=0; j< NUM_SIM_STEPS; j++){
			try {
				Thread.sleep(50);
				prices.add(stocks.get(0).getPointAvg().get(20));
				balA.add(players.get(0).getBalance());
				balB.add(players.get(1).getBalance());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

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
		System.out.println("");
		lb.get();

	}

	public static void main(String args[]) {
		open();
		testExchange();
	}
}