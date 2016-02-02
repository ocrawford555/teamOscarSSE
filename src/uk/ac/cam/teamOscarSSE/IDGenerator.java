package uk.ac.cam.teamOscarSSE;

public class IDGenerator {
	static private int currentID = 1;
	
	private IDGenerator() {}
	
	public static int getID() {
		return currentID++;
	}
}
