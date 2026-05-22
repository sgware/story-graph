package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * A custom implementation of a singly linked list. This implementation used
 * very little memory, but most operations run in linear time. For example, the
 * list does not store its size, so the {@link #size()} method must iterate over
 * all elements to count them. It is expected that collections using this
 * implementation will be small.
 * 
 * @param <T> type type of element stored in the list
 * @author Stephen G. Ware
 */
public abstract class LinkedList<T> implements Iterable<T> {
	
	/** The first element in the list */
	private T first = null;
	
	/**
	 * Creates a new empty linked list.
	 */
	public LinkedList() {
		// default constructor
	}
	
	/**
	 * An {@link Iterator} for {@link LinkedList}.
	 * 
	 * @author Stephen G. Ware
	 */
	private class LinkedListIterator implements Iterator<T> {
		
		/** The next element to be returned by {@link #next()} */
		private T current = first;
		
		@Override
		public boolean hasNext() {
			return current != null;
		}
		
		@Override
		public T next() {
			if(!hasNext())
				throw Exceptions.iteratorEmpty();
			T next = current;
			current = getNext(current);
			return next;
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return new LinkedListIterator();
	}
	
	/**
	 * Returns the number of elements currently stored in the list. This
	 * operation runs in linear time.
	 * 
	 * @return the number of elements in the list
	 */
	public int size() {
		return Utilities.size(this);
	}
	
	/**
	 * Checks whether an element is in the list. This method runs in linear
	 * time.
	 * 
	 * @param element the element to search for
	 * @return true if the element is in the list, false otherwise
	 */
	public boolean contains(T element) {
		return Utilities.contains(this, element);
	}
	
	/**
	 * Returns the element at the given index. This method runs in linear time.
	 * 
	 * @param index the index of the desired element
	 * @return the element at that index
	 * @throws IndexOutOfBoundsException if the index does not exist
	 */
	public T get(int index) {
		return Utilities.get(this, index);
	}
	
	/**
	 * For some element in the list, this method returns the next element in the
	 * list, or null if the given element was the last element.
	 * 
	 * @param element some element in the list
	 * @return the element after the given element, or null if the given element
	 * was last
	 */
	abstract T getNext(T element);
	
	/**
	 * This element links a given previous element to a given next element in
	 * the list.
	 * 
	 * @param previous an element whose next element will be set
	 * @param next the element which will now be the next element in the list or
	 * null if the previous element will be the last element
	 */
	abstract void setNext(T previous, T next);
	
	/**
	 * Adds an element to the end of the list. This method runs in linear time.
	 * 
	 * @param element the element to add to the list
	 */
	void add(T element) {
		if(first == null)
			first = element;
		else {
			T previous = null;
			for(T e : this)
				previous = e;
			setNext(previous, element);
		}
	}
	
	/**
	 * Removes the first occurrence of an element from this list.
	 * 
	 * @param element the element to remove from the list
	 */
	void remove(T element) {
		if(first == element)
			first = getNext(element);
		else {
			for(T previous : this) {
				if(getNext(previous) == element) {
					setNext(previous, getNext(element));
					return;
				}
			}
		}
	}
}