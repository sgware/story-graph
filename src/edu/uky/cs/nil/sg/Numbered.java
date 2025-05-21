package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * A {@link StoryGraph story graph} element with a unique sequential {@code
 * long} {@link #getID() ID number}. ID numbers are unique for the type of
 * element, but not across the whole story graph. For example, a story graph
 * cannot have two {@link TemporalEdge temporal edges} with the same ID number,
 * but it can a temporal edge and an {@link EpistemicEdge epistemic edge} with
 * the same ID number.
 * <p>
 * A large story graph may have many numbered elements, so their ID numbers are
 * Java {@code long} integers. Numbered elements are stored in a custom {@link
 * NumberedList large array list} that can have up to {@link
 * BigArrayList#MAX_CAPACITY} elements.
 * 
 * @author Stephen G. Ware
 */
public class Numbered {
	
	/** The element's unique ID number */
	private long id;
	
	/**
	 * Constructs a new numbered element with the given ID number.
	 * 
	 * @param id a unique ID number
	 */
	protected Numbered(long id) {
		this.id = id;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The hash code of a numbered element is based on its {@link #getID() ID
	 * number} (specifically, it is calculated using {@link Long#hashCode()}).
	 * Because ID numbers are unique and sequential among numbered elements of
	 * the same type, they can be a perfect hash function. However, when a story
	 * graph is modified (for example, {@link
	 * StoryGraph#prune(Predicate, Status) pruned}) elements may be renumbered.
	 * If elements are stored based on their hash codes (for example, in a
	 * {@link java.util.HashMap} hash table), their hash codes may be unreliable
	 * after the graph is modified.
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
	
	@Override
	public String toString() {
		return Long.toString(id);
	}
	
	/**
	 * Returns the unique sequential ID number for this element. ID numbers are
	 * unique among numbered elements of the same type, but not unique among the
	 * story graph as a whole. For example, a story graph cannot have two {@link
	 * TemporalEdge temporal edges} with the same ID number, but it can have a
	 * temporal edge and an {@link EpistemicEdge epistemic edge} with the same
	 * ID number.
	 * 
	 * @return the unique ID number
	 */
	public long getID() {
		return id;
	}
	
	/**
	 * Sets the ID number of the element. ID numbers should be unique and
	 * sequential among elements of the same type.
	 * 
	 * @param id the new ID number
	 */
	void setID(long id) {
		this.id = id;
	}
	
	/**
	 * Removes any pruned elements from this element, and if this element itself
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