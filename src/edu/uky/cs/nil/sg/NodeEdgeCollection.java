package edu.uky.cs.nil.sg;

import java.util.Iterator;

/**
 * A {@link NodeEdgeIterable collection} of all {@link Edge edges} that have a
 * specific {@link Node node} as their {@link Edge#tail tail} or {@link
 * Edge#head head}.
 * 
 * @author Stephen G. Ware
 */
public class NodeEdgeCollection implements NodeEdgeIterable<Edge> {
	
	/** The node's temporal edges */
	public final NodeEdgeGroup<TemporalEdge> temporal = new NodeEdgeGroup<>();
	
	/** The node's epistemic edges */
	public final NodeEdgeGroup<EpistemicEdge> epistemic = new NodeEdgeGroup<>();
	
	/**
	 * Constructs a new node edge collection.
	 */
	protected NodeEdgeCollection() {}
	
	@Override
	public String toString() {
		return "[Edge Collection: " + temporal.size() + " temporal edges; " + epistemic.size() + " epistemic edges]";
	}
	
	@Override
	public Iterator<Edge> iterator() {
		return new MergeIterator<Edge>(temporal.iterator(), epistemic.iterator());
	}
	
	@Override
	public int size() {
		return temporal.size() + epistemic.size();
	}
}