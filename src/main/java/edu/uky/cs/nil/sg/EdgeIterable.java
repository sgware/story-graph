package edu.uky.cs.nil.sg;

/**
 * A collection of {@link Edge edges}.
 * 
 * @param <E> the type of edge in the collection
 * @author Stephen G. Ware
 */
public interface EdgeIterable<E extends Edge> extends Iterable<E> {
	
	/**
	 * Checks whether this collection of edges contains the given edge.
	 * 
	 * @param edge the edge which may be in this collection
	 * @return true if the edge appears in this collection, false otherwise
	 */
	public default boolean contains(E edge) {
		return contains(edge.tail, edge.label, edge.head);
	}
	
	/**
	 * Checks whether this collection of edges contains an edge with the given
	 * tail node, label, and head node.
	 * 
	 * @param tail the tail node of the edge that may be in this collection
	 * @param label the label the edge that may be in this collection
	 * @param head the head node of the edge that may be in this collection
	 * @return true if the edge appears in this collection, false otherwise
	 */
	public default boolean contains(Node tail, Object label, Node head) {
		return get(tail, label, head) != null;
	}
	
	/**
	 * Returns the {@link Edge edge object} from this collection that has the
	 * given tail node, label, and head node, or null if no such edge exists.
	 * 
	 * @param tail the tail node of the edge that may be in this collection
	 * @param label the label the edge that may be in this collection
	 * @param head the head node of the edge that may be in this collection
	 * @return the edge object with that tail, label, and head, or null if no
	 * such edge exists
	 */
	public E get(Node tail, Object label, Node head);
}