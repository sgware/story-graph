package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@link StoryGraphList collection} of {@link Numbered numbered} story graph
 * elements of the same type. An element can be accessed by its {@link
 * #get(long) ID number}.
 * <p>
 * This collection stores its elements in a {@link BigArrayList custom array
 * list} that can hold up to {@link BigArrayList#MAX_CAPACITY} elements.
 * 
 * @param <N> the type of numbered element in this collection
 * @author Stephen G. Ware
 */
public abstract class NumberedList<N extends Numbered> extends StoryGraphList<N> {
	
	/** The list in which the elements are stored by ID number */
	private final BigArrayList<N> list;
	
	/**
	 * Constructs a new numbered element list. If the {@link StoryGraph#meta
	 * story graph's meta-data} specifies the number of elements in the list,
	 * it will be initialized with exactly that capacity.
	 * 
	 * @param graph the story graph to which the numbered elements belong
	 */
	protected NumberedList(StoryGraph graph) {
		super(graph);
		Long size = graph.meta.getLong(getMetaDataKey());
		if(size == null)
			list = new BigArrayList<>();
		else
			list = new BigArrayList<>(size);
	}
	
	@Override
	public String toString() {
		String string = "[" + Utilities.capitalize(getSingular()) + " List";
		String name = graph.meta.getString(MetaData.TITLE);
		if(name != null)
			string += " for \"" + name + "\"";
		string += ": " + size() + " " + getPlural() + "]";
		return string;
	}
	
	@Override
	public Iterator<N> iterator() {
		return list.iterator();
	}
	
	@Override
	public void forEach(Consumer<? super N> consumer) {
		list.forEach(consumer);
	}
	
	@Override
	protected String getFileName() {
		return getPlural() + ".csv";
	}
	
	/**
	 * Returns the number of elements in the collection.
	 * 
	 * @return the number of elements
	 */
	public long size() {
		return list.size();
	}
	
	/**
	 * Returns the element with the given {@link Numbered#getID() ID number}.
	 * 
	 * @param index the unique ID number of the desired element
	 * @return the element with that ID number
	 * @throws IndexOutOfBoundsException if the index is negative
	 * @throws IllegalArgumentException if the index is greater than or equal to
	 * the {@link #size() size} of the collection
	 */
	public N get(long index) {
		if(index < 0)
			throw Exceptions.indexOutOfBounds(index, size());
		else if(index >= size())
			throw Exceptions.idNotDefined(getSingular(), index);
		else
			return list.get(index);
	}
	
	@Override
	protected boolean validate(Object object) {
		return object instanceof Numbered n && n.getID() >= 0 && n.getID() < size() && get(n.getID()) == n;
	}
	
	/**
	 * Puts a new element into this collection at the index of {@link
	 * Numbered#getID() its ID number}. It is expected that the element's ID
	 * number will be the {@link #size() size} of this collection at the time
	 * it is added, otherwise an existing element will be overridden or null
	 * elements will be added to the collection.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * with the new number of elements of {@link #getMetaDataKey() this type}.
	 * It also updates the {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param numbered the element to add
	 */
	protected void add(N numbered) {
		list.set(numbered.getID(), numbered);
		graph.meta.set(getMetaDataKey(), size());
		graph.meta.set(MetaData.MODIFIED, Instant.now());
	}
	
	/**
	 * Sorts and renumbers the elements of this collection based on the given
	 * comparator. If the order of any elements changes, their {@link
	 * Numbered#getID() ID numbers} will be reassigned to be sequential.
	 * <p>
	 * If this method modifies the order of elements, it updates the {@link
	 * StoryGraph#meta story graph's meta-data} {@link MetaData#MODIFIED last
	 * modified} timestamp.
	 * 
	 * @param comparator the comparator that defines the new order of elements
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @return true if any elements were reordered, false if all elements
	 * remained in the same order
	 */
	public boolean sort(Comparator<? super N> comparator, Status status) {
		status.set("Sorting " + getPlural(), (long) size());
		if(size() > 0)
			status.setCount(1);
		boolean modified = false;
		for(long start = 1; start < size(); start++) {
			for(long i = start - 1; i >= 0; i--) {
				if(comparator.compare(get(i), get(i + 1)) > 0) {
					N temp = list.get(i);
					list.set(i, list.get(i + 1));
					list.set(i + 1, temp);
					modified = true;
				}
				else
					break;
			}
			status.increment();
		}
		status.setMessage("Sorted " + status.getCount() + " " + getPlural());
		if(modified) {
			renumber(status);
			graph.meta.set(MetaData.MODIFIED, Instant.now());
		}
		return modified;
	}
	
	/**
	 * {@link #sort(Comparator, Status) Sorts and renumbers} the elements in
	 * this collection without reporting the method's progress while it runs.
	 * 
	 * @param comparator the comparator that defines the new order of elements
	 * @return true if any elements were reordered, false if all elements
	 * remained in the same order
	 * @see #sort(Comparator, Status)
	 */
	public boolean sort(Comparator<? super N> comparator) {
		return sort(comparator, new Status());
	}
	
	@Override
	protected boolean prune(Predicate<Object> predicate, Status status) {
		status.set("Pruning " + getPlural(), size());
		boolean modified = false;
		for(N numbered : this) {
			numbered.prune(predicate);
			if(numbered.getID() == Settings.PRUNED)
				modified = true;
			status.increment();
		}
		status.setMessage("Pruned " + status.getCount() + " " + getPlural());
		return modified;
	}
	
	@Override
	protected void renumber(Status status) {
		status.set("Renumbering " + getPlural(), size());
		int nextID = 0;
		for(long i = 0; i < size(); i++) {
			N numbered = list.get(i);
			if(numbered.getID() != Settings.PRUNED) {
				numbered.setID(nextID);
				list.set(nextID++, numbered);
			}
			status.increment();
		}
		while(list.size() > nextID)
			list.remove(size() - 1);
		graph.meta.set(getMetaDataKey(), size());
		status.setMessage("Renumbered " + status.getCount() + " " + getPlural());
	}
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		super.read(reader, status);
		list.clear();
	}
}