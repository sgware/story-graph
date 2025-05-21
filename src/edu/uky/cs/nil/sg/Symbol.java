package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * A {@link StoryGraph story graph} element with a unique sequential {@code
 * int} {@link #getID() ID number}, a unique {@link #name name}, and an optional
 * {@link #getComment() comment}. ID numbers and names are unique for the type
 * of symbol, but not across the whole story graph. For example, a story graph
 * cannot have two {@link Character characters} with the same name, but it can
 * have a character and a {@link NominalValue value} with the same name.
 * <p>
 * A story graph is expected to have relatively few symbols relative to other
 * elements like {@link Node nodes} and {@link Edge edges}.
 * 
 * @author Stephen G. Ware
 */
public abstract class Symbol implements Commented {
	
	/** The symbol's unique name */
	public final String name;
	
	/** The symbol's unique ID number */
	private int id;
	
	/** The symbol's comment */
	private String comment = null;
	
	/**
	 * Constructs a new symbol with the given ID number and name.
	 * 
	 * @param id a unique ID number
	 * @param name a unique name
	 */
	protected Symbol(int id, String name) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Symbols return their {@link #getID() ID numbers} as their hash codes.
	 * Because ID numbers are unique and sequential among symbols of the same
	 * type, they are a perfect hash function. However, when a story graph is
	 * modified (for example, {@link StoryGraph#prune(Predicate, Status)
	 * pruned}) symbols may be renumbered. If symbols are stored based on their
	 * hash codes (for example, in a {@link java.util.HashMap} hash table),
	 * their hash codes may be unreliable after the graph is modified.
	 */
	@Override
	public int hashCode() {
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Symbols return their unique {@link #name names} when converted to string.
	 */
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String getComment() {
		return comment;
	}
	
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Returns the unique sequential ID number for this symbol. ID numbers are
	 * unique among symbols of the same type, but not unique among the story
	 * graph as a whole. For example, a story graph cannot have two {@link
	 * Character characters} with the same ID number, but it can have a
	 * character and a {@link Value value} with the same ID number.
	 * 
	 * @return the unique ID number
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Sets the ID number of the symbol. ID numbers should be unique and
	 * sequential among symbols of the same type.
	 * 
	 * @param id the new ID number
	 */
	void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Removes any pruned elements from this symbol, and if this symbol itself
	 * should be pruned its ID number will be set to {@link Settings#PRUNED}.
	 * 
	 * @param predicate a predicate which returns true for any story graph
	 * element that should be removed
	 */
	protected void prune(Predicate<Object> predicate) {
		if(predicate.test(this))
			setID(Settings.PRUNED);
	}
}