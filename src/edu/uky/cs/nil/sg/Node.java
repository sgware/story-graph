package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * A node represents a specific moment in a {@link StoryGraph story graph}; it
 * defines a {@link Value value} for every {@link Fluent fluent}, a numeric
 * utility value for the story and each {@link Character character}, possible
 * futures represented by its {@link NodeEdgeCollection#temporal temporal
 * edges}, and the beliefs of each character represented by its {@link
 * NodeEdgeCollection#epistemic epistemic edges}.
 * 
 * @author Stephen G. Ware
 */
public class Node extends Numbered implements Comparable<Node>, Commented {
	
	/**
	 * All of the {@link Edge edges} that have this node as a {@link Edge#tail}
	 * or {@link Edge#head}
	 */
	public final NodeEdgeCollection edges = new NodeEdgeCollection();
	
	/** The {@link Explanation explanations} associated with this node */
	public final NodeExplanationList explanations = new NodeExplanationList();
	
	/** The fluent and utility values for this node */
	private State state;
	
	/** The comment associated with this node */
	private String comment = null;
	
	/**
	 * Constructs a new node with the given ID number and state.
	 * 
	 * @param id the unique sequential ID number of the node
	 * @param state the fluent values and utilities of the node
	 */
	protected Node(long id, State state) {
		super(id);
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "[Node " + getID() + "]";
	}
	
	@Override
	public int compareTo(Node other) {
		return Long.compare(this.getID(), other.getID());
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
	 * Returns the {@link State state} which defines the {@link Fluent fluent}
	 * {@link Value values} and {@link State#getUtility(Character) utility
	 * values} for this node.
	 * 
	 * @return the node's state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * Sets the node's state.
	 * 
	 * @param state the new state for this node
	 */
	void setState(State state) {
		this.state = state;
	}
	
	/**
	 * Returns this node's {@link Value value} for a given {@link Fluent
	 * fluent}.
	 * 
	 * @param fluent the fluent whose value is desired
	 * @return the value for the fluent in this state
	 * @see State#getValue(Fluent)
	 */
	public Value getValue(Fluent fluent) {
		return getState().getValue(fluent);
	}
	
	/**
	 * Returns this node's author's utility, which is how desirable the node's
	 * state is for the story as a whole.
	 * 
	 * @return the author's utility
	 */
	public double getUtility() {
		return getUtility(null);
	}
	
	/**
	 * Returns this node's utility for the given {@link Character character},
	 * which is how desirable that character considers this node to be.
	 * 
	 * @param character the character whose utility is desired
	 * @return the character's utility
	 */
	public double getUtility(Character character) {
		return getState().getUtility(character);
	}
	
	/**
	 * Returns the {@link Node node} after taking the given {@link Action
	 * action}, if one is defined by the node's {@link
	 * NodeEdgeCollection#temporal temporal edges}, or null if one is not
	 * defined.
	 * 
	 * @param action an action
	 * @return the node after the action is taken, if one is defined, or null
	 */
	public Node getAfter(Action action) {
		TemporalEdge edge = edges.temporal.out.get(action);
		if(edge == null)
			return null;
		else
			return edge.head;
	}
	
	/**
	 * Returns the {@link Node node} after taking a series of actions, if it is
	 * defined by the graph's {@link EdgeCollection#temporal temporal edges}, or
	 * null if one is not defined.
	 * 
	 * @param plan a series of actions
	 * @return the node after those actions are taken, if one is defined, or
	 * null
	 */
	public Node getAfter(Iterable<Action> plan) {
		Node after = this;
		for(Action action : plan) {
			after = after.getAfter(action);
			if(after == null)
				break;
		}
		return after;
	}
	
	/**
	 * Returns the {@link Node node} that represents what a given {@link
	 * Character character} believes the state of the world to be if one is
	 * defined by the node's {@link NodeEdgeCollection#epistemic epistemic
	 * edges}, or null if one is not defined.
	 * 
	 * @param character the character whose beliefs are desired
	 * @return the node representing the character's beliefs
	 */
	public Node getBeliefs(Character character) {
		EpistemicEdge edge = edges.epistemic.out.get(character);
		if(edge == null)
			return null;
		else
			return edge.head;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * A node will be pruned if its {@link #getState() state} has its ID number
	 * set to {@link Settings#PRUNED}.
	 */
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		if(getState().getID() == Settings.PRUNED)
			setID(Settings.PRUNED);
	}
}