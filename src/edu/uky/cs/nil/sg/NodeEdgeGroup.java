package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * Contains both the {@link #in incoming} and {@link #out outgoing} edges of a
 * certain type for a {@link Node node}.
 * 
 * @param <E> the type of edge
 * @author Stephen G. Ware
 */
public class NodeEdgeGroup<E extends Edge> implements NodeEdgeIterable<E> {
	
	/** The edges that have the node as their {@link Edge#head head} */
	public final NodeEdgeList.In<E> in = new NodeEdgeList.In<>();
	
	/** The edges that have the node as their {@link Edge#tail tail} */
	public final NodeEdgeList.Out<E> out = new NodeEdgeList.Out<>();
	
	@Override
	public String toString() {
		return "[Edge Group: " + in.size() + " in edges; " + out.size() + " out edges]";
	}

	@Override
	public Iterator<E> iterator() {
		return new MergeIterator<>(in.iterator(), out.iterator());
	}
	
	@Override
	public int size() {
		return in.size() + out.size();
	}
}