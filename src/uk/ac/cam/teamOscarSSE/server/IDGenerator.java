package uk.ac.cam.teamOscarSSE.server;

/**
 * IDGenerator class generates unique order IDs 
 * for all of the players and bots. Class is
 * thread safe.
 */

public class IDGenerator {
	static private long currentID = 1;

	private IDGenerator() {}

	public static synchronized long getID() {
		return currentID++;
	}
}
