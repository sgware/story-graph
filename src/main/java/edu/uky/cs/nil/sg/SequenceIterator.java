package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * Iterates through a {@link Sequence sequence} using the {@link
 * Sequence#get(int) index} of each element.
 * 
 * @author Stephen G. Ware
 */
public class SequenceIterator implements Iterator<Action> {
	
	/** The sequence whose actions are being iterated through */
	private final Sequence sequence;
	
	/** The index of the action to return next */
	private int index = 0;
	
	/**
	 * Constructs a sequence iterator for the given sequence.
	 * 
	 * @param sequence the sequence whose actions will be iterated through
	 */
	public SequenceIterator(Sequence sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public boolean hasNext() {
		return index < sequence.size();
	}
	
	@Override
	public Action next() {
		if(hasNext())
			return sequence.get(index++);
		else
			throw Exceptions.iteratorEmpty();
	}
}