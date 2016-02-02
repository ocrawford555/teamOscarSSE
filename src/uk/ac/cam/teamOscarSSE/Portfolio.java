package uk.ac.cam.teamOscarSSE;

import java.util.Map;
import java.util.TreeMap;

public class Portfolio {
	//portfolio class for now will presume you can only hold one stock
	private Map<Stock,Integer> stockOwned = new TreeMap<Stock,Integer>();

	public Portfolio() {}

	public void add(Stock s, int numStocks){
		if(stockOwned.containsKey(s))
			stockOwned.put(s, stockOwned.get(s) + numStocks);
		else
			stockOwned.put(s, numStocks);
	}

	public void remove(Stock s, int numStocks){
		if(stockOwned.containsKey(s))
			stockOwned.put(s, stockOwned.get(s) - numStocks);
	}

	public boolean check(Stock s, int amountToTrade){
		if(stockOwned.containsKey(s)){
			int currentAmount = stockOwned.get(s);
			if ((currentAmount - amountToTrade) < 0){
				return false;
			}
			else{
				return true;
			}
		}
		else{
			return false;
		}
	}

	public double currentValue(){
		double value = 0.0;
		for(Map.Entry<Stock, Integer> entry : stockOwned.entrySet()){
			double stockValue = entry.getKey().getLastTransactionPrice();
			value+=(stockValue) * entry.getValue();
		}
		return value;
	}
}
