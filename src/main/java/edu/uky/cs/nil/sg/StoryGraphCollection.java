package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * A collection of {@link StoryGraph story graph} elements.
 * 
 * @param <T> the type of story graph element in this collection
 * @author Stephen G. Ware
 */
public abstract class StoryGraphCollection<T> implements Iterable<T> {
	
	/** The story graph to which this collection and its elements belongs */
	protected final StoryGraph graph;
	
	/**
	 * Constructs a new story graph collection for the given story graph.
	 * 
	 * @param graph the story graph to which this collection and its elements
	 * belongs
	 */
	StoryGraphCollection(StoryGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * Returns a word or phrase describing the type of a single element stored
	 * in this collection. This description is used in relevant exceptions and
	 * {@link Status status messages} used by this collection.
	 * 
	 * @return a word or phrase describing a single elements of this collection
	 */
	protected abstract String getSingular();
	
	/**
	 * Returns a word or phrase describing the type of a multiple elements
	 * stored in this collection. This description is used in relevant
	 * exceptions and {@link Status status messages} used by this collection.
	 * <p>
	 * By default, this method appends {@code "s"} to the result of {@link
	 * #getSingular()}.
	 * 
	 * @return a word or phrase describing multiple elements of this collection
	 */
	protected String getPlural() {
		return getSingular() + "s";
	}
	
	/**
	 * Returns true if the given element is a member of this collection.
	 * 
	 * @param object the object which may be a member of this collection
	 * @return true of the element is a member of this collection, false
	 * otherwise
	 * @see #require(Object)
	 */
	protected abstract boolean validate(Object object);
	
	/**
	 * Throws an exception if the given element is not a member of this
	 * collection according to {@link #validate(Object)}.
	 * 
	 * @param element the element which may be a member of this collection
	 * @throws IllegalArgumentException if the element is not a member
	 */
	protected void require(T element) {
		if(!validate(element))
			throw Exceptions.invalid(getSingular(), element);
	}
	
	/**
	 * Removes a single element from this collection. This method may modify
	 * other elements of the {@link StoryGraph story graph} and may run for a
	 * long time as a result. For example, when a single {@link Node node} is
	 * removed from a story graph, all {@link Edge edges} incident to that node
	 * are also removed.
	 * <p>
	 * If the graph was modified by this method, the elements in this collection
	 * and other elements of the graph will be renumbered.
	 * 
	 * @param element the element to remove
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @return true if the element was returned and the graph was modified,
	 * false if nothing was removed and the graph was not modified
	 */
	public boolean remove(T element, Status status) {
		return remove(object -> Utilities.equals(object, element), status);
	}
	
	/**
	 * {@link #remove(Object, Status) Removes} a single element from this
	 * collection without reporting the method's progress while it runs.
	 * 
	 * @param element the element to remove
	 * @return true if the element was returned and the graph was modified,
	 * false if nothing was removed and the graph was not modified
	 * @see #remove(Object, Status)
	 */
	public boolean remove(T element) {
		return remove(element, new Status());
	}
	
	/**
	 * Removes all elements from this collection for which the given predicate
	 * returns true. This method may modify other elements of the {@link
	 * StoryGraph story graph} and may run for a long time as a result. For
	 * example, when {@link Node nodes} are removed from a story graph, all
	 * {@link Edge edges} incident to those nodes are also removed.
	 * <p>
	 * If the graph was modified by this method, the elements in this collection
	 * and other elements of the graph will be renumbered.
	 * 
	 * @param predicate a predicate that specifies which elements should be
	 * removed
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @return true if any elements were removed and the graph was modified,
	 * false if nothing was removed and the graph was not modified
	 */
	public boolean remove(Predicate<? super T> predicate, Status status) {
		return removeAndPrune(predicate, status);
	}
	
	/**
	 * {@link #remove(Predicate, Status) Removes} all elements from this
	 * collection for which the given predicate returns true without reporting
	 * the method's progress while it runs.
	 * 
	 * @param predicate a predicate that specifies which elements should be
	 * removed
	 * @return true if any elements were removed and the graph was modified,
	 * false if nothing was removed and the graph was not modified
	 * @see #remove(Predicate, Status)
	 */
	public boolean remove(Predicate<? super T> predicate) {
		return remove(predicate, new Status());
	}
	
	/**
	 * This method {@link #prune(Predicate, Status) prunes} all elements from
	 * this collection for which the given predicate returns true and then
	 * prunes other relevant story graph collections, renumbering any that are
	 * modified.
	 * 
	 * @param predicate a predicate that specifies which elements should be
	 * removed from this collection
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @param collections other story graph collections that might have been
	 * affected when elements were removed from this list and which may need to
	 * be pruned and renumbered as a result
	 * @return true if any elements were removed and the graph was modified,
	 * false if nothing was removed and the graph was not modified
	 */
	@SuppressWarnings("unchecked")
	boolean removeAndPrune(Predicate<? super T> predicate, Status status, StoryGraphCollection<?>...collections) {
		if(prune(object -> validate(object) && predicate.test((T) object), status)) {
			boolean[] renumber = new boolean[collections.length];
			for(int i = 0; i < collections.length; i++)
				renumber[i] = collections[i].prune(object -> false, status);
			renumber(status);
			for(int i = 0; i < collections.length; i++)
				if(renumber[i])
					collections[i].renumber(status);
			graph.meta.set(MetaData.MODIFIED, Instant.now());
			return true;
		}
		else
			return false;
	}
	
	/**
	 * For each element in this collection, this method removes any reference in
	 * that element to other elements whose ID numbers have been set to {@link
	 * Settings#PRUNED} and, if the given predicate returns true, sets the
	 * element's ID number to {@link Settings#PRUNED} to signal that it should
	 * also be removed from this graph. This method does not remove elements
	 * from this collection for which the predicate returns true; it only sets
	 * their ID numbers to signal that they should later be removed, which
	 * typically happens when the collection is {@link #renumber(Status)
	 * renumbered}.
	 * 
	 * @param predicate a predicate that specifies which elements should be
	 * removed from from a story graph
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @return true if the predicate returned true for any elements in this
	 * collection
	 */
	protected abstract boolean prune(Predicate<Object> predicate, Status status);
	
	/**
	 * Removes any elements from this collection whose ID numbers have been set
	 * to {@link Settings#PRUNED} and sets the ID numbers of all remaining
	 * elements so that their ID numbers start at 0, are unique, and are
	 * sequential.
	 * 
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 */
	protected abstract void renumber(Status status);
	
	/**
	 * Reads the elements of this collection from a {@link GraphReader graph
	 * reader}.
	 * 
	 * @param reader a graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading the files or if
	 * the files are not formatted correctly
	 */
	protected abstract void read(GraphReader reader, Status status) throws IOException;
	
	/**
	 * Writes the elements of this collection to a {@link GraphWriter graph
	 * writer}.
	 * 
	 * @param writer a graph writer
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing the files
	 */
	protected abstract void write(GraphWriter writer, Status status) throws IOException;
}