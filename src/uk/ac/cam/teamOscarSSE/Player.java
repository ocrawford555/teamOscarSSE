package uk.ac.cam.teamOscarSSE;

import java.util.UUID;

public class Player {
	//human name of competitor
	private String name;

	//unique ID matched to competitor, generated
	//at creation of an instance of User object
	private String token;

	//email address of the user
	private String emailAddress;

	//amount of cash owned by the player at any given time
	private long cashLeft;

	//link to portfolio of stocks owned
	private Portfolio pf;

	//private Algo algo;

	public String getName() {
		return name;
	}

	public String getToken() {
		return token;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public long getBalance() {
		return cashLeft + pf.currentValue();
	}
	
	public Portfolio getPortfoio(){
		return pf;
	}

	// TODO: argument won't actually be an OrderUpdateMessage.
	public void updatePortfolio(OrderUpdateMessage orderUpdate) {
		Stock tradedStock = orderUpdate.order.getStock();

		if(orderUpdate.order.getOrderType() == OrderType.BUY){
			pf.add(tradedStock, orderUpdate.size);
			//as player is buying stocks, they are spending money
			updateCash(-1 * (orderUpdate.size * orderUpdate.price));
			//System.out.println("Cash lost: " + (-1 * (orderUpdate.size * orderUpdate.price)));
		}
		else if (orderUpdate.order.getOrderType() == OrderType.SELL){
			pf.remove(tradedStock, orderUpdate.size);
			//as player is selling stocks, they gain cash
			updateCash(orderUpdate.size * orderUpdate.price);
			//System.out.println("Cash gained: " + (orderUpdate.size * orderUpdate.price));
		}
	}

	//this method can both increase cash or decrease cash
	//depending on sign
	public void updateCash(long cash) {
		cashLeft += cash;
	}

	public Player(String name, String email) {
		this.name = name;
		this.emailAddress = email;
		//generate random 32 hex characters (128 bit) token
		this.token = UUID.randomUUID().toString().replaceAll("-", "");
		//start with 10,000,000 cents (or pennies, depending on currency)
		this.cashLeft = 10000000;
		this.pf = new Portfolio();
	}
}
