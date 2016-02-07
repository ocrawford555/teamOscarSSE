package uk.ac.cam.teamOscarSSE;

import java.util.Random;

public abstract class Bot {
	
	protected Stock stock;
	protected Exchange ex;
	protected int TMAX;
	protected Random r = new Random();
	
	public Bot(Exchange e, Stock s) {
		ex = e;
		stock = s;
		TMAX = s.getTMAX();
	}
	
	public boolean submitOrder(Order order) {
		return ex.addOrder(order);
	}
	
	public abstract void sendOrders();
	

}
