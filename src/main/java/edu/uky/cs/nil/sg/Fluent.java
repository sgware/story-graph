package edu.uky.cs.nil.sg;

/**
 * A fluent is a {@link StoryGraph story graph} {@link Symbol symbol} that
 * represents a variable in a {@link State world state} can can have one of
 * several possible {@link Value values} which might change when an {@link
 * Action action} happens.
 * <p>
 * New fluents should be created in a story graph using the {@link
 * FluentList#add(String)} method.
 * 
 * @author Stephen G. Ware
 */
public class Fluent extends Symbol implements Comparable<Fluent> {
	
	/**
	 * Constructs a new fluent symbol with the given ID number and name.
	 * 
	 * @param id the fluent's unique sequential ID number
	 * @param name the fluent's unique name
	 */
	protected Fluent(int id, String name) {
		super(id, name);
	}
	
	@Override
	public int compareTo(Fluent other) {
		return Integer.compare(this.getID(), other.getID());
	}
}