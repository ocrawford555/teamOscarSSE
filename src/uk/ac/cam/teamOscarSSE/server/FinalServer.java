package uk.ac.cam.teamOscarSSE.server;

import uk.ac.cam.teamOscarSSE.server.bots.*;

import java.io.IOException;
import java.util.ArrayList;

public class FinalServer {
	static final Exchange exchange = new Exchange();
	static ArrayList<Stock> stocks = new ArrayList<Stock>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static LeaderBoard lb;

	// The number of simulation steps.
	private static int NUM_SIM_STEPS = 600;
	private static int roundLength = 30;
	private static int timeBetweenRounds = 30;

	public static void open() {
		stocks.clear();
		Stock stock1 = new Stock("BAML", "Bank of America", 5000,0.35f,3482,400);
		stocks.add(stock1);

		//create the leader board
		lb = new LeaderBoard(players);

		//add the players to the exchange
		for (Player player : players) {
			exchange.addPlayer(player);
		}

		// Open the exchange
		exchange.startRound(stocks, roundLength, timeBetweenRounds);
	}

	public static void testExchange(int scenario) {
		//create fake two user algorithms
		//both algorithms in theory could be ported to provide framework
		//for the eventual user of the competition
		//		PennyingAlgo penny = new PennyingAlgo(exchange,stocks,players);
		//		Thread user1 = new Thread(penny);
		//		RandomTrading random = new RandomTrading(exchange,stocks,players);
		//		Thread user2 = new Thread(random);
		//		FairPriceGuess fpg = new FairPriceGuess(exchange,stocks,players);
		//		Thread user3 = new Thread(fpg);


		//include a non-aggressive market maker to just set up some orders, and then
		//add some orders to the order book occasionally - not in the game for
		//profit -> this bot is simulating normal consumers looking to buy and 
		//sell stocks.
		MarketMaker mm = new MarketMaker(exchange, stocks.get(0),40,40,200);

		//general bot in play for simplification only
		GeneralBot gb = new GeneralBot(exchange, stocks.get(0));

		//price moving bot
		PriceMovingBot pmb = new PriceMovingBot(exchange,stocks.get(0));

		//scenario bot - changes depending on the type of market that is being
		//simulated
		Bot scenarioBot;

		switch(scenario){
		case 1:
			scenarioBot = new GeneralBot(exchange, stocks.get(0));
			break;
		case 2:
			scenarioBot = new BoomBot(exchange, stocks.get(0));
			break;
		case 3:
			scenarioBot = new RecessionBot(exchange, stocks.get(0));
			break;
		default:
			scenarioBot = new BoomBot(exchange, stocks.get(0));
			break;
		}

		Thread marketM = new Thread(mm);
		Thread generalBot = new Thread(gb);
		Thread priceMover = new Thread(pmb);
		Thread scBot = new Thread(scenarioBot);

		//start the trading
		marketM.start();
		generalBot.start();
		priceMover.start();
		scBot.start();
	}

	public static void close() {
		exchange.endRound();
		lb.update();
	}

	public static void main(String args[]) {
		try {
			NewServer.start(8080, exchange);
		} catch (IOException e) {
			System.out.println("Failed to start the server.");
			e.printStackTrace();
			return;
		}

		while (true) {
			//off for debugging, useful for demo day and want to run
			//different scenarios maybe??
			//could automate this if we have time
			boolean choice = false;
			
			//default scenario is boom
			int roundToPlay = 2;
			
			if(choice){
				System.out.println("Team Oscar -- Simulated Stock Exchange");
				System.out.println("Options for round:");
				System.out.println("1.\t Normal");
				System.out.println("2.\t Boom");
				System.out.println("3.\t Bust");
				try {
					roundToPlay = System.in.read();
				} catch (IOException e) {
					System.err.println(
							"Failed to read roundToPlay. Continuing with option 2 (boom).");
					e.printStackTrace();
				}
			}

			open();
			testExchange(roundToPlay);
			try {
				Thread.sleep(roundLength * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			close();
			try {
				Thread.sleep(timeBetweenRounds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}