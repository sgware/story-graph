package edu.uky.cs.nil.sg;

import java.io.IOException;

/**
 * The {@link EdgeList list} of {@link TemporalEdge temporal edges} in a {@link
 * StoryGraph story graph}, which represent how {@link Action actions} can
 * change the state of a story from one {@link Node node} to another.
 * 
 * @author Stephen G. Ware
 */
public class TemporalEdgeList extends EdgeList<TemporalEdge> {
	
	/**
	 * Constructs a new temporal edge list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's temporal edges belong
	 */
	protected TemporalEdgeList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.TEMPORAL;
	}
	
	@Override
	protected String getSingular() {
		return "temporal edge";
	}
	
	@Override
	protected String getFileName() {
		return "temporal.csv";
	}
	
	@Override
	protected String getCommentFileName() {
		return "temporal_comments.txt";
	}
	
	/**
	 * Creates a new temporal edge that extends from a tail {@link Node node} to
	 * a head node and is labeled with an {@link Action action}.
	 * <p>
	 * If a temporal edge already exists from the tail node for this action and
	 * a different head node is given, this method throws an exception. If the
	 * same head node is given, the existing edge is returned.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param before the tail node, representing the story state before the
	 * action
	 * @param action the action that can be taken
	 * @param after the head node, representing the story state after the action
	 * is taken
	 * @return a new temporal edge with the given tail, label, and head, or the
	 * existing edge if that exact edge already exists
	 * @throws IllegalArgumentException if an edge already exists from the tail
	 * node for this action but not to the given head node
	 * @throws IllegalArgumentException if the tail node, action, or head node
	 * have been removed or were not created in this list's story graph
	 */
	public TemporalEdge add(Node before, Action action, Node after) {
		graph.nodes.require(before);
		graph.actions.require(action);
		graph.nodes.require(after);
		TemporalEdge edge = before.edges.temporal.out.get(action);
		if(edge == null) {
			edge = new TemporalEdge(size(), before, action, after);
			add(edge);
			before.edges.temporal.out.add(edge);
			after.edges.temporal.in.add(edge);
		}
		else if(edge.head != after)
			throw Exceptions.duplicateEdge(getSingular(), before, action);
		return edge;
	}
	
	@Override
	protected void readEdge(Node tail, int label, Node head) throws IOException {
		add(tail, graph.actions.get(label), head);
	}
}