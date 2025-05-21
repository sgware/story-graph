package edu.uky.cs.nil.sg;

import java.util.Set;
import java.util.function.Predicate;

/**
 * A temporal edge models how the {@link State state} of a {@link Node node}
 * changes after an {@link Action action} occurs. The tail node is the state
 * before the action occurs. The label is the action. The head node is the state
 * after the action occurs.
 * <p>
 * New temporal edges should be created in a story graph using the {@link
 * TemporalEdgeList#add(Node, Action, Node)} method.
 * 
 * @author Stephen G. Ware
 */
public class TemporalEdge extends Edge implements Comparable<TemporalEdge>, Consenting {
	
	/**
	 * A collection of {@link Explanation explanations} for a particular {@link
	 * TemporalEdge temporal edge}. Removing an explanation from a {@link Node
	 * node} for this edge also removed the explanation from this collection.
	 * 
	 * @author Stephen G. Ware
	 */
	public class TemporalEdgeExplanationList extends ExplanationSubset {
		
		/**
		 * Constructs a new temporal edge explanation list.
		 */
		protected TemporalEdgeExplanationList() {
			super(tail.explanations, explanation -> explanation.startsWith(label));
		}
		
		@Override
		public String toString() {
			return "[Explanation List for " + label + ": " + size() + " explanations]";
		}
		
		/**
		 * Returns the explanations for this temporal edge for a given {@link
		 * Character character}.
		 * 
		 * @param character the character whose explanations are desired
		 * @return a collection of explanations for this temporal edge and the
		 * given character
		 */
		public ExplanationSubset get(Character character) {
			return new ExplanationSubset(this, explanation -> Utilities.equals(explanation.character, character));
		}
	}
	
	/**
	 * The action that occurs in the tail state and results in the head state
	 */
	public final Action label;
	
	/** All {@link Explanation explanations} for this temporal edge */
	public TemporalEdgeExplanationList explanations = new TemporalEdgeExplanationList();
	
	/**
	 * Constructs a new temporal edge with the given ID number, tail node, head
	 * node, and action label.
	 * 
	 * @param id the unique sequential ID number of the edge
	 * @param before the node representing the state before the action
	 * @param action the action that occurs
	 * @param after the node representing that state after the action
	 */
	protected TemporalEdge(long id, Node before, Action action, Node after) {
		super(id, before, action, after);
		this.label = action;
	}
	
	@Override
	public String toString() {
		return "[Temporal Edge " + getID() + ": " + tail.getID() + " -(" + label + ")-> " + head.getID() + "]";
	}
	
	@Override
	public int compareTo(TemporalEdge other) {
		return Long.compare(this.getID(), other.getID());
	}
	
	@Override
	public Set<Character> consenting() {
		return label.consenting;
	}
	
	/**
	 * Checks whether an {@link Explanation explanation} exists for each of the
	 * {@link Action#consenting consenting characters} of this edge's {@link
	 * #label action}.
	 * 
	 * @return true if an explanation exists for each consenting character,
	 * false otherwise
	 */
	public boolean isExplained() {
		for(Character consenting : consenting())
			if(!isExplained(consenting))
				return false;
		return true;
	}
	
	/**
	 * Checks whether an {@link Explanation explanation} for this edge's {@link
	 * #label action} exists for the given {@link Character character}.
	 * 
	 * @param character the character
	 * @return true if an explanation exists for that character, false otherwise
	 */
	public boolean isExplained(Character character) {
		return explanations.get(character).size() > 0;
	}
	
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		if(getID() == Settings.PRUNED) {
			tail.edges.temporal.out.remove(this);
			head.edges.temporal.in.remove(this);
		}
	}
}