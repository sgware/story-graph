package edu.uky.cs.nil.sg;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A {@link Iterable subset} of {@link Explanation explanations} that belong to
 * a specific {@link Node node}.
 * 
 * @author Stephen G. Ware
 */
public class ExplanationSubset implements Iterable<Explanation> {
	
	/** The full set of explanations */
	private final Iterable<Explanation> explanations;
	
	/** A predicate that specifies which explanations are part of this subset */
	private final Predicate<? super Explanation> predicate;
	
	/**
	 * Constructs a new subset of explanations from a collection of explanations
	 * and a predicate that specifies which explanations are part of the subset.
	 * 
	 * @param explanations the original set of explanations
	 * @param predicate a predicate that returns true for each explanation that
	 * belongs in this subset
	 */
	public ExplanationSubset(Iterable<Explanation> explanations, Predicate<? super Explanation> predicate) {
		this.explanations = explanations;
		this.predicate = predicate;
	}
	
	@Override
	public String toString() {
		return "[Explanation Subset: " + size() + " explanations]";
	}
	
	@Override
	public Iterator<Explanation> iterator() {
		return new FilterIterator<>(explanations.iterator(), predicate);
	}
	
	/**
	 * Returns the number of {@link Explanation explanations} in this subset.
	 * This method runs in linear time.
	 * 
	 * @return the number of explanations
	 */
	public int size() {
		return Utilities.size(this);
	}
	
	/**
	 * Returns true if a given {@link Explanation explanation} is in this
	 * subset. This method runs in linear time.
	 * 
	 * @param explanation an explanation to search for
	 * @return true if the explanation is in this subset, false otherwise
	 */
	public boolean contains(Explanation explanation) {
		return Utilities.contains(this, explanation);
	}
	
	/**
	 * Returns the {@link Explanation explanation} at the given index. This
	 * method runs in linear time.
	 * 
	 * @param index the index of the desired explanation
	 * @return the explanation at that index
	 * @throws IndexOutOfBoundsException if the index does not exist
	 */
	public Explanation get(int index) {
		return Utilities.get(this, index);
	}
}