package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;

public class StockManager {
	private static ArrayList<Stock> stocks;
	private static StockManager instance = null;
	
	protected StockManager(ArrayList<Stock> st) {
		stocks = st;
	}
	
	public static StockManager getInstance(ArrayList<Stock> st){
		if(instance == null){
			instance = new StockManager(st);
		}
		return instance;
	}
	
	public static ArrayList<Stock> getStocks(){
		return stocks;
	}
	
	public void printStocks(){
		System.out.println("--- STOCKS PRESENT TO TRADE ---");
		for(Stock s: stocks){
			System.out.println(s.getSymbol() + " -- " + s.getName());
		}
		System.out.println("---"); System.out.println("");
	}
}
