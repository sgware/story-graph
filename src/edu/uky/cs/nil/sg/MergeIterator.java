package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * An {@link Iterator iterator} that returns all elements of two other
 * iterators.
 * 
 * @param <T> the type of elements this iterator will return
 */
public class MergeIterator<T> implements Iterator<T> {
	
	/** The first iterator whose elements will be returned */
	private final Iterator<? extends T> first;
	
	/** The second iterator whose elements will be returned */
	private final Iterator<? extends T> second;
	
	/**
	 * Constructs a new iterator that will return all elements of two other
	 * iterators (all element from the first, followed by all element from the
	 * second).
	 * 
	 * @param first the first iterator whose elements should be returned by this
	 * iterator
	 * @param second the second iterator whose elements should be returned by
	 * this iterator
	 */
	public MergeIterator(Iterator<? extends T> first, Iterator<? extends T> second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean hasNext() {
		return first.hasNext() || second.hasNext();
	}
	
	@Override
	public T next() {
		if(first.hasNext())
			return first.next();
		else
			return second.next();
	}
}