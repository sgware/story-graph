package edu.uky.cs.nil.sg;

/**
 * A {@link LinkedList collection} of {@link Edge edges} of the same type
 * associated with a specific {@link Node node}.
 * 
 * @param <E> the type of edge in the collection
 * @author Stephen G. Ware
 */
public abstract class NodeEdgeList<E extends Edge> extends LinkedList<E> implements NodeEdgeIterable<E> {
	
	/**
	 * A {@link NodeEdgeList collection} of {@link Edge edges} of the same type
	 * which have the same {@link Node node} as their {@link Edge#head head}.
	 * 
	 * @param <E> the type of edge in the collection
	 * @author Stephen G. Ware
	 */
	public static class In<E extends Edge> extends NodeEdgeList<E> {
		
		/**
		 * Creates a new empty node edge list of incoming edges.
		 */
		protected In() {
			// default constructor
		}
		
		@Override
		public String toString() {
			return "[In Edge List: " + size() + " edges]";
		}
		
		@Override
		@SuppressWarnings("unchecked")
		E getNext(E edge) {
			return (E) edge.nextIn;
		}
		
		@Override
		void setNext(E previous, E next) {
			previous.nextIn = next;
		}
		
		/**
		 * Returns the edge with the given {@link Edge#tail tail node} and
		 * {@link Edge#label label} and which has this collection's node as the
		 * {@link Edge#head head node}, or null if no such edge exists.
		 * 
		 * @param tail the tail node
		 * @param label the edge label
		 * @return the edge with the given tail and label that has this
		 * collection's node as the head, or null if no such edge exists
		 */
		public E get(Object tail, Object label) {
			for(E edge : this)
				if(Utilities.equals(edge.tail, tail) && Utilities.equals(edge.label, label))
					return edge;
			return null;
		}
	}
	
	/**
	 * A {@link NodeEdgeList collection} of {@link Edge edges} of the same type
	 * which have the same {@link Node node} as their {@link Edge#tail tail}.
	 * 
	 * @param <E> the type of edge in the collection
	 * @author Stephen G. Ware
	 */
	public static class Out<E extends Edge> extends NodeEdgeList<E> {
		
		/**
		 * Creates a new empty node edge list of outgoing edges.
		 */
		protected Out() {
			// default constructor
		}
		
		@Override
		public String toString() {
			return "[Out Edge List: " + size() + " edges]";
		}
		
		@Override
		@SuppressWarnings("unchecked")
		E getNext(E edge) {
			return (E) edge.nextOut;
		}
		
		@Override
		void setNext(E previous, E next) {
			previous.nextOut = next;
		}
		
		/**
		 * Returns the edge with the given {@link Edge#label label} and which
		 * has this collection's node as the {@link Edge#tail tail node}, or
		 * null if no such edge exists.
		 * 
		 * @param label the edge label
		 * @return the edge with this collection's node as the tail and the
		 * given label, or null if no such edge exists
		 */
		public E get(Object label) {
			for(E edge : this)
				if(Utilities.equals(edge.label, label))
					return edge;
			return null;
		}
	}
	
	/**
	 * Creates a new empty node edge list.
	 */
	protected NodeEdgeList() {
		// default constructor
	}
}