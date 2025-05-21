package edu.uky.cs.nil.sg;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A wrapper around an {@link Iterator} that returns only the elements for which
 * a {@link Predicate} returns true.
 * 
 * @param <T> the type of element the iterator will return
 * @author Stephen G. Ware
 */
public class FilterIterator<T> implements Iterator<T> {
	
	/** The wrapped iterator that will be filtered */
	private final Iterator<T> iterator;
	
	/**
	 * The predicate that defines which elements from the iterator will be
	 * returned
	 */
	private final Predicate<? super T> predicate;
	
	/** The element to be returned by the next call to {@link #next()} */
	private T next = null;
	
	/**
	 * Constructs a new filter iterator with a given iterator and predicate.
	 * 
	 * @param iterator the original iterator whose elements will be filtered
	 * @param predicate the predicate which defines which elements will be
	 * returned by this iterator
	 */
	public FilterIterator(Iterator<T> iterator, Predicate<? super T> predicate) {
		this.iterator = iterator;
		this.predicate = predicate;
		advance();
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}
	
	@Override
	public T next() {
		T next = this.next;
		advance();
		return next;
	}
	
	private void advance() {
		next = null;
		while(iterator.hasNext()) {
			T element = iterator.next();
			if(predicate.test(element)) {
				next = element;
				return;
			}
		}
	}
}