package uk.ac.cam.teamOscarSSE;

public class IDGenerator {
	static private long currentID = 1;

	private IDGenerator() {
	}

	public static long getID() {
		return currentID++;
	}
}
