package uk.ac.cam.teamOscarSSE;

// Make users write their code to buy and at what volume in Main method and initialise and call user class to run code 
public class User {
	
	static Stock s;
	static Exchange e;
	
	
	public static void main(String[] args) {
		UserFramework a = new UserFramework(s,e) {
			
			@Override
			public boolean Buy(){
				//Algorithm when to buy shares
				
				return false;
			}
			@Override
			public int volumeToBuy() {
				//Algorithm how many shares to buy
				
				return 0;
			}
			@Override
			public boolean Sell(){
				//Algorithm when to sell shares
				
				return false;
			}
			@Override
			public int volumeToSell() {
				//Algorithm how many shares to buy
				
				return 0;
			}
		};
		
		Thread user1 = new Thread(a);
		user1.start();
		
		
	}
}
