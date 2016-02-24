package uk.ac.cam.teamOscarSSE.client;

// Make users write their code to buy and at what volume in Main method and initialise and call user class to run code 
public class User {
	
	/*
	 * Metrics:
	 * pointAvg List
	 * overallAvg
	 * transactionAvg List
	 * rateOfChange List
	 */
	public static void main(String[] args) {
		UserFrameServer a = new UserFrameServer("Akkash") {
			
			@Override
			public boolean Buy(){
				//return (getPointAvg(5) > getPointAvg(20));
				return false;
			}
			@Override
			public int volumeToBuy() {
				//Algorithm how many shares to buy
				return (getMaxBuy()/100);
			}
			@Override
			public boolean Sell(){
				//Algorithm when to sell shares
				//return (getPointAvg(5) < getPointAvg(20));
				return false;
			}
			@Override
			public int volumeToSell() {
				//Algorithm how many shares to buy
				return (getMaxSell()/100);
			}
		};
		
		Thread user1 = new Thread(a);
		user1.start();
		
		UserFrameServer b = new UserFrameServer("Aniruddh") {
			
			@Override
			public boolean Buy(){
				return (getPointAvg(5) > getPointAvg(20));
			}
			@Override
			public int volumeToBuy() {
				//Algorithm how many shares to buy
				return (getMaxBuy()/151);
			}
			@Override
			public boolean Sell(){
				//Algorithm when to sell shares
				return (getPointAvg(5) < getPointAvg(20));
			}
			@Override
			public int volumeToSell() {
				//Algorithm how many shares to buy
				return (getMaxSell()/150);
			}
		};
		
		Thread user2 = new Thread(b);
		user2.start();
	}
}
