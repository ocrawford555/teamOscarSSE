package teamOscarSSE;

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
	private double cashLeft;
	
	//link to portfolio of stocks owned
	private Portfolio pf;
	
	private Algo algo;

	public String getName() {
		return name;
	}

	public String getToken() {
		return token;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	
	public double balance(){
		 return cashLeft + pf.currentValue();
		 
	}
	
	//this method can both increase cash or decrease cash
	//depending on sign
	public void updateCash(double cash){
		cashLeft+=cash;
	}

	public Player(String name, String email){
		this.name = name;
		this.emailAddress = email;
		//generate random 32 hex characters (128 bit) token
		this.token = UUID.randomUUID().toString().replaceAll("-", "");
		this.cashLeft = 10000;
		this.pf = new Portfolio();
		this.algo = new Algo();
		this.algo.run("Pennying");
	}
}
