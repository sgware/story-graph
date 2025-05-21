package edu.uky.cs.nil.sg;

/**
 * A {@link EdgeIterable collection} of some {@link Edge edges} related to a
 * specific {@link Node node}. The collection of edges related to a single node
 * is assumed to be small enough that its {@link #size() size} can be
 * represented by an {@code int} and the collection can be easily {@link
 * #get(int) searched} for find the edge at a specific index.
 * 
 * @param <E> the type of edge in the collection
 * @author Stephen G. Ware
 */
public interface NodeEdgeIterable<E extends Edge> extends EdgeIterable<E> {
	
	@Override
	public default E get(Node tail, Object label, Node head) {
		for(E edge : this)
			if(Utilities.equals(tail, edge.tail) && Utilities.equals(label, edge.label) && Utilities.equals(head, edge.head))
				return edge;
		return null;
	}
	
	/**
	 * Returns the number of edges in the collection.
	 * 
	 * @return the number of edges
	 */
	public int size();
	
	/**
	 * Returns the edge at the given index from an collection. The first element
	 * has index 0, the second has index 1, etc. This index does not correspond
	 * to the edge's ID number.
	 * 
	 * @param index the index of the edge in this collection
	 * @return the edge at that index
	 */
	public default E get(int index) {
		return Utilities.get(this, index);
	}
}