package uk.ac.cam.teamOscarSSE;

import java.util.*;
import java.util.Map.Entry;

public class LeaderBoard {
	//keep track of score for all the players
	ArrayList<Player> playersInRound = new ArrayList<Player>();
	Map<String, Long> lB = new HashMap<String, Long>();

	public LeaderBoard(ArrayList<Player> players) {
		for (Player p : players) {
			playersInRound.add(p);
			lB.put(p.getName(), p.getBalance() - p.getStartingCash());
		}
	}

	static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
					@Override
					public int compare(Entry<K, V> e1, Entry<K, V> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				}
		);
		return sortedEntries;
	}

	//method called by server to update leader board
	public void update() {
		for (Player p : playersInRound) {
			lB.put(p.getName(), p.getBalance() - p.getStartingCash());
		}
	}

	//method called by web interface to get latest
	//results
	//first of all, hashmap has to be sorted in
	//descending order using the entriesSortedByValues
	//function.
	public void get() {
		update();
		List<Entry<String, Long>> sorted = entriesSortedByValues(lB);

		for (Entry<String, Long> e : sorted) {
			System.out.println("Player: " + e.getKey() + " --- " + "Profit / Loss: " + e.getValue());
		}
	}

	//send email to winner of the round, with score
	//not major priority currently
	public void sendEmail() {}	

	//arguably not required, future versions may
	//choose to save results from round on to a
	//permanent store
	public void saveResults() {}
}