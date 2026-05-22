package edu.uky.cs.nil.sg;

/**
 * Constants used by the story graph library.
 * 
 * @author Stephen G. Ware
 */
public class Settings {
	
	/**
	 * The major version number, which represents big changes that are not
	 * backwards compatible with earlier major versions
	 */
	public static final int MAJOR_VERSION_NUMBER = 1;
	
	/**
	 * The minor version number, which represents added functionality that is
	 * backwards compatible with earlier versions that have the same major
	 * version number
	 */
	public static final int MINOR_VERSION_NUMBER = 0;
	
	/**
	 * The patch version number, which represents bug fixes but no changes in
	 * functionality
	 */
	public static final int PATCH_VERSION_NUMBER = 0;
	
	/** The full version number (major + minor) as a string */
	public static final String VERSION_STRING = MAJOR_VERSION_NUMBER + "." + MINOR_VERSION_NUMBER + "." + PATCH_VERSION_NUMBER;
	
	/**
	 * Story graph elements that should be removed will have their ID numbers
	 * set to this value during pruning but before they are fully removed from
	 * the graph
	 */
	public static final int PRUNED = -1;
	
	private Settings() {}
}