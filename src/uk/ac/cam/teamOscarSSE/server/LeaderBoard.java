package uk.ac.cam.teamOscarSSE.server;

import java.util.*;
import java.util.Map.Entry;

/**
 * The LeaderBoard class keeps info about the leaderboard.
 */
public class LeaderBoard {
	//List of players in the competition
	ArrayList<Player> playersInRound = new ArrayList<Player>();
	
	//Map of the scores of each player
	Map<String, Long> lB = new HashMap<String, Long>();

	public LeaderBoard(ArrayList<Player> players) {
		for (Player p : players) {
			playersInRound.add(p);
			lB.put(p.getName(), p.getBalance() - p.getStartingCash());
		}
	}
	
	/**
	 * 
	 * @param map
	 * 	takes unsorted leaderboard and sorts them
	 * @return
	 * 	the sorted entries
	 */
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

	/**
	 * Update contents of the leaderboard. Iterate through
	 * each player, changing their P/L score.
	 */
	public void update() {
		for (Player p : playersInRound) {
			lB.put(p.getName(), p.getBalance() - p.getStartingCash());
		}
	}

	/**
	 * To get the correct leaderboard, first update,
	 * and then sort, and then output.
	 */
	public void get() {
		update();
		List<Entry<String, Long>> sorted = entriesSortedByValues(lB);

		for (Entry<String, Long> e : sorted) {
			System.out.println("Player: " + e.getKey() + " --- " + "Profit / Loss: " + e.getValue());
		}
	}

	//not for current version; future implementation
	public void sendEmail() {}	

	//not for current version; future implementation
	public void saveResults() {}
}