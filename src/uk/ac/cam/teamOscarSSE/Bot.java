package uk.ac.cam.teamOscarSSE;

import java.util.Random;

public abstract class Bot {
	
	protected Stock stock;
	protected Exchange2 ex;
	protected int TMAX;
	protected Player player;
	protected Random r = new Random();
	
	public Bot(Exchange2 e, Stock s, Player p) {
		ex = e;
		stock = s;
		TMAX = s.getTMAX();
		player = p;
	}
	
	public boolean sumbitOrder(Order order) {
		return ex.addOrder(order);
	}
	
	public abstract void sendOrders();
}
