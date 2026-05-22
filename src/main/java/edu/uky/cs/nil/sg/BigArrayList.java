package edu.uky.cs.nil.sg;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A custom implementation of an array list which can hold up to {@link
 * #MAX_CAPACITY} elements and which uses long integers to index elements and
 * to measure its {@link #size()}.
 * 
 * @param <T> type of element stored in the list
 * @author Stephen G. Ware
 */
public class BigArrayList<T> implements Iterable<T> {
	
	/** The maximum size of an individual array, as in integer */
	private static final int MAX_ARRAY_SIZE_INT = (int) Math.pow(2, 20);
	
	/** The maximum size of an individual array, as a long */
	private static final long MAX_ARRAY_SIZE_LONG = MAX_ARRAY_SIZE_INT;
	
	/**
	 * The maximum number of elements that can be stored in this kind of list
	 */
	public static final long MAX_CAPACITY = MAX_ARRAY_SIZE_LONG * MAX_ARRAY_SIZE_LONG;
	
	/**
	 * The default number of elements that can be stored in the list before new
	 * arrays must be allocated
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 1024;
	
	/**
	 * For some index in the list, this method returns the index of the array in
	 * which that element will be stored.
	 * <p>
	 * Because a Java array is limited to {@link Integer#MAX_VALUE} elements,
	 * this list is implemented as an array of arrays. To find an element in
	 * this list based on its index, first use {@link #array(long)} to find
	 * the array the element is in, and then use {@link #position(long)} to find
	 * the index in that array where the element is stored.
	 * 
	 * @param index an index in the list
	 * @return the index of the array in which that list index is stored
	 */
	private static final int array(long index) {
		return Math.toIntExact(index / MAX_ARRAY_SIZE_LONG);
	}
	
	/**
	 * For some index in the list, this method returns the index in {@link
	 * #array(long) the array} where the element will be stored.
	 * <p>
	 * Because a Java array is limited to {@link Integer#MAX_VALUE} elements,
	 * this list is implemented as an array of arrays. To find an element in
	 * this list based on its index, first use {@link #array(long)} to find
	 * the array the element is in, and then use {@link #position(long)} to find
	 * the index in that array where the element is stored.
	 * 
	 * @param index an index in the list
	 * @return the index of the element in its array
	 */
	private static final int position(long index) {
		return Math.toIntExact(index % MAX_ARRAY_SIZE_LONG);
	}
	
	/** The array of arrays where elements are stored */
	private Object[][] arrays = new Object[0][];
	
	/**
	 * The number of elements that can be stored in this list without needing to
	 * allocate new arrays
	 */
	private long capacity = 0;
	
	/** The current number of elements stored in the list */
	private long size = 0;
	
	/**
	 * Constructs a new big array list with the given capacity.
	 * 
	 * @param capacity the number of elements that can be stored in the list
	 * before new arrays need to be allocated
	 */
	public BigArrayList(long capacity) {
		setCapacity(capacity);
	}
	
	/**
	 * Constructs a new big array list with the {@link #DEFAULT_INITIAL_CAPACITY
	 * default initial capacity}.
	 */
	public BigArrayList() {
		this(DEFAULT_INITIAL_CAPACITY);
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof BigArrayList<?> otherBAL) {
			if(this.size() != otherBAL.size())
				return false;
			Iterator<?> i1 = this.iterator();
			Iterator<?> i2 = otherBAL.iterator();
			while(i1.hasNext())
				if(!Utilities.equals(i1.next(), i2.next()))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for(T element : this)
			code = code * 31 + Utilities.hashCode(element);
		return code;
	}
	
	@Override
	public String toString() {
		return "[Big Array List: " + size() + " elements]";
	}
	
	/**
	 * An iterator for a {@link BigArrayList}.
	 * 
	 * @author Stephen G. Ware
	 */
	private class BigArrayListIterator implements Iterator<T> {
		
		/** The next index in the list to be returned by {@link #next()} */
		private long index = 0;
		
		@Override
		public boolean hasNext() {
			return index < size();
		}
		
		@Override
		public T next() {
			if(!hasNext())
				throw Exceptions.iteratorEmpty();
			return get(index++);
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return new BigArrayListIterator();
	}
	
	@Override
	public void forEach(Consumer<? super T> consumer) {
		for(long i = 0; i < size(); i++)
			consumer.accept(get(i));
	}
	
	/**
	 * Returns the number of elements currently stored in the list.
	 * 
	 * @return the number of elements in the list
	 */
	public long size() {
		return size;
	}
	
	/**
	 * Returns the number of elements that can be stored in the list without
	 * allocating new arrays.
	 * 
	 * @return the capacity of the list
	 */
	protected long capacity() {
		return capacity;
	}
	
	/**
	 * Returns the index of the first occurrence of the given element in the
	 * list or -1 if the element is not in the list.
	 * 
	 * @param element the element whose index is desired
	 * @return the index of the first element that is equal to the given object
	 * or -1 if no such element exists
	 */
	public long indexOf(T element) {
		for(long index = 0; index < size(); index++)
			if(Utilities.equals(get(index), element))
				return index;
		return -1;
	}
	
	/**
	 * Returns the element at the given index.
	 * 
	 * @param index the index of the desired element
	 * @return the element at that index
	 * @throws IndexOutOfBoundsException if the index does not exist in the list
	 */
	@SuppressWarnings("unchecked")
	public T get(long index) {
		if(index < 0 || index >= size())
			throw Exceptions.indexOutOfBounds(index, size());
		else
			return (T) arrays[array(index)][position(index)];
	}
	
	/**
	 * Sets the element at the given index, overwriting the element that was
	 * previous at the index, if any. If the index already exists in the list,
	 * the element at that index will be replaced and the {@link #size() size}
	 * of the list will not change. If the index is greater than or equal to the
	 * size of the list, the list (and its size) is expanded until the index
	 * exists and then the element is placed at that index. Any other indices
	 * created because of this expansion will be set to null.
	 * 
	 * @param index the index where the element should be placed
	 * @param element the element to be placed at that index
	 */
	public void set(long index, T element) {
		if(index < 0)
			throw Exceptions.indexOutOfBounds(index, size());
		long capacity = Math.max(this.capacity(), 1);
		if(index >= MAX_CAPACITY)
			capacity = index + 1;
		else {
			while(index >= capacity) {
				if(capacity < MAX_ARRAY_SIZE_LONG)
					capacity = Math.min(capacity * 2, MAX_ARRAY_SIZE_LONG);
				else
					capacity += MAX_ARRAY_SIZE_LONG;
			}
		}
		setCapacity(capacity);
		arrays[array(index)][position(index)] = element;
		size = Math.max(size, index + 1);
	}
	
	/**
	 * Adds an element to the end of the list.
	 * 
	 * @param element the element to add to the list
	 */
	public void add(T element) {
		set(size(), element);
	}
	
	/**
	 * Adds an element to a specific index in the list, shifting all elements
	 * after that index one to the right to make room for it.
	 * 
	 * @param index the index at which the element will be added
	 * @param element the element to add at that index
	 * @throws IndexOutOfBoundsException if the index does not exist in the list
	 */
	public void add(long index, T element) {
		set(size(), null);
		for(long i = size() - 1; i > index; i++)
			set(i, get(i - 1));
		set(index, element);
	}
	
	/**
	 * Removes the element at the given index from the list, shifting all
	 * elements after that index one to the left.
	 * 
	 * @param index the index of the element to be removed
	 * @throws IndexOutOfBoundsException if the index does not exist in the list
	 */
	public void remove(long index) {
		if(index < 0 || index >= size())
			throw Exceptions.indexOutOfBounds(index, size());
		for(long i = index; i < size() - 1; i++)
			set(i, get(i + 1));
		set(size() - 1, null);
		size--;
	}
	
	/**
	 * Removes the first occurrence of the given element from the list.
	 * 
	 * @param element the element to be removed
	 */
	public void remove(T element) {
		long index = indexOf(element);
		if(index != -1)
			remove(index);
	}
	
	/**
	 * Removes all elements from the list.
	 */
	public void clear() {
		for(long i = 0; i < size(); i++)
			set(i, null);
		size = 0;
	}
	
	/**
	 * Modifies the list to have exactly the given capacity. If the capacity is
	 * smaller than the {@link #size() size}, elements will be deleted.
	 * 
	 * @param capacity the number of elements that list will be able to store
	 * before new arrays need to be allocated
	 * @throws IllegalArgumentException if the desired capacity exceeds {@link
	 * #MAX_CAPACITY}
	 */
	private void setCapacity(long capacity) {
		if(capacity == this.capacity)
			return;
		else if(capacity > MAX_CAPACITY)
			throw Exceptions.listCapacityTooHigh();
		Object[][] arrays = new Object[array(capacity - 1) + 1][];
		int i = 0;
		while(i + MAX_ARRAY_SIZE_INT <= capacity && i + MAX_ARRAY_SIZE_INT <= this.capacity) {
			arrays[array(i)] = this.arrays[array(i)];
			i += MAX_ARRAY_SIZE_INT;
		}
		while(i < capacity) {
			int size = (int) Math.min(MAX_ARRAY_SIZE_INT, capacity - i);
			if(i < size())
				arrays[array(i)] = Arrays.copyOf(this.arrays[array(i)], size);
			else
				arrays[array(i)] = new Object[size];
			i += size;
		}
		this.arrays = arrays;
		this.capacity = capacity;
	}
	
	/**
	 * Sorts the elements of this list to be in the order defined by the given
	 * comparator.
	 * 
	 * @param comparator an object which can compare two objects to determine
	 * which should come first in the new order
	 */
	public void sort(Comparator<? super T> comparator) {
		quicksort(comparator, 0, size());
	}
	
	private void quicksort(Comparator<? super T> comparator, long start, long end) {
		if(end - start <= 1)
			return;
		long pivot = partition(comparator, start, end);
		quicksort(comparator, start, pivot);
		quicksort(comparator, pivot + 1, end);
	}
	
	private long partition(Comparator<? super T> comparator, long start, long end) {
		T pivot = get(end - 1);
		long index = start;
		for(long i = start; i < end -1; i++) {
			if(comparator.compare(get(i), pivot) <= 0) {
				swap(i, index);
				index++;
			}
		}
		swap(end - 1, index);
		return index;
	}
	
	private final void swap(long i1, long i2) {
		T temp = get(i1);
		set(i1, get(i2));
		set(i2, temp);
	}
}