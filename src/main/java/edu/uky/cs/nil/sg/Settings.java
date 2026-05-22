package edu.uky.cs.nil.sg;

/**
 * Constants used by the story graph library.
 * 
 * @author Stephen G. Ware
 */
public class Settings {
	
	/** The major version number comes before the decimal points */
	public static final int MAJOR_VERSION_NUMBER = 0;
	
	/** The minor version number comes after the decimal point */
	public static final int MINOR_VERSION_NUMBER = 9;
	
	/** The full version number (major + minor) as a string */
	public static final String VERSION_STRING = MAJOR_VERSION_NUMBER + "." + MINOR_VERSION_NUMBER;
	
	/**
	 * Story graph elements that should be removed will have their ID numbers
	 * set to this value during pruning but before they are fully removed from
	 * the graph
	 */
	public static final int PRUNED = -1;
	
	private Settings() {}
}