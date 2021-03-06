package uk.ac.cam.teamOscarSSE.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Portfolio {
	//portfolio class for now will presume you can only hold one stock
	private Map<Stock, Integer> stockOwned = new HashMap<Stock, Integer>();
	private Map<Stock, Integer> stockBorrowed = new HashMap<Stock, Integer>();

	public Portfolio() {
	}

	public void add(Stock s, int numStocks) {
		if (stockOwned.containsKey(s))
			stockOwned.put(s, stockOwned.get(s) + numStocks);
		else
			stockOwned.put(s, numStocks);
	}

	public void remove(Stock s, int numStocks) {
		if (stockOwned.containsKey(s)) {
			stockOwned.put(s, stockOwned.get(s) - numStocks);
		} else {
			stockOwned.put(s, -numStocks);
		}
	}

	public int getAmountOwned(Stock stock) {
		Integer amount = stockOwned.get(stock);
		if (amount == null) {
			return 0;
		} else {
			return amount;
		}
	}

	public void borrowShort(Stock s, int borrowAmount) {
		if (stockBorrowed.containsKey(s))
			stockBorrowed.put(s, stockBorrowed.get(s) + borrowAmount);
		else
			stockBorrowed.put(s, borrowAmount);
	}

	public void sellShort(Stock s, int sellAmount) {
		if (stockBorrowed.containsKey(s))
			stockBorrowed.put(s, stockBorrowed.get(s) - sellAmount);
	}

	public boolean check(Stock s, int amountToTrade) {
		if (stockOwned.containsKey(s)) {
			int currentAmount = stockOwned.get(s);
			return (currentAmount - amountToTrade) >= 0;
		} else {
			return false;
		}
	}

	public long currentValue() {
		long value = 0;
		for (Map.Entry<Stock, Integer> entry : stockOwned.entrySet()) {
			long stockValue = entry.getKey().getStockPrice();
			value += (stockValue) * entry.getValue();
		}
		return value;
	}

	public void contents() {
		Iterator it = stockOwned.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Stock, Integer> pair = (Entry<Stock, Integer>) it.next();
			System.out.println(pair.getKey().getSymbol() + "  ---->   " + pair.getValue());
		}
	}

	public Map<Stock, Integer> getOwnedStock() {
		return stockOwned;
	}

	public Map<Stock, Integer> getBorrowedStock() {
		return stockBorrowed;
	}
}
