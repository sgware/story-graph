package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * An epistemic edge models what a {@link Character character} believes a {@link
 * Node node's} {@link State state} to be. The tail node is the actual state.
 * The label is a character. The head node is the state the character believes
 * to be the case.
 * <p>
 * New epistemic edges should be created in a story graph using the {@link
 * EpistemicEdgeList#add(Node, Character, Node)} method.
 * 
 * @author Stephen G. Ware
 */
public class EpistemicEdge extends Edge implements Comparable<EpistemicEdge> {
	
	/** The character whose beliefs this edge represents */
	public final Character label;
	
	/**
	 * Constructs a new epistemic edge with the given ID number, tail node, head
	 * node, and character label.
	 * 
	 * @param id the unique sequential ID number of the edge
	 * @param actual the node representing the actual state
	 * @param character a character
	 * @param beliefs the node representing the state the character believes to
	 * be the case
	 */
	protected EpistemicEdge(long id, Node actual, Character character, Node beliefs) {
		super(id, actual, character, beliefs);
		this.label = character;
	}
	
	@Override
	public String toString() {
		return "[Epistemic Edge " + getID() + ": " + tail.getID() + " -(" + label + ")-> " + head.getID() + "]";
	}
	
	@Override
	public int compareTo(EpistemicEdge other) {
		return Long.compare(this.getID(), other.getID());
	}
	
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		if(getID() == Settings.PRUNED) {
			tail.edges.epistemic.out.remove(this);
			head.edges.epistemic.in.remove(this);
		}
	}
}