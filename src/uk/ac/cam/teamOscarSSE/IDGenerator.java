package uk.ac.cam.teamOscarSSE;

public class IDGenerator {
	static private long currentID = 1;

	private IDGenerator() {}

	public static synchronized long getID() {
		return currentID++;
	}
}
