package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * An edge represents a directed relationship from a {@link #tail tail} {@link
 * Node node} to a {@link #head} node with a {@link #label label} in a {@link
 * StoryGraph story graph}.
 * 
 * @author Stephen G. Ware
 */
public abstract class Edge extends Numbered implements Commented {
	
	/** The source node from which this edge leads */
	public final Node tail;
	
	/**
	 * The label that explains the relationship between the tail and head nodes
	 */
	public final Symbol label;
	
	/** The destination node to which this edge leads */
	public final Node head;
	
	/** The next in edge of this type in a {@link NodeEdgeList} */
	Edge nextIn = null;
	
	/** The next out edge of this type in a {@link NodeEdgeList} */
	Edge nextOut = null;
	
	/** The comment associated with the edge */
	private String comment = null;
	
	/**
	 * Constructs a new edge from a given tail node, head node, and label.
	 * 
	 * @param id the unique sequential ID number of the edge
	 * @param tail the node from which the edge leads
	 * @param label the label which explained the relationship
	 * @param head the node to which the edge leads
	 */
	protected Edge(long id, Node tail, Symbol label, Node head) {
		super(id);
		this.tail = tail;
		this.label = label;
		this.head = head;
	}
	
	@Override
	public String getComment() {
		return comment;
	}
	
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * An edge will be pruned if its tail node, label, or head node have their
	 * ID numbers set to {@link Settings#PRUNED}.
	 */
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		if(tail.getID() == Settings.PRUNED || label.getID() == Settings.PRUNED || head.getID() == Settings.PRUNED)
			setID(Settings.PRUNED);
	}
}