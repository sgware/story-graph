package edu.uky.cs.nil.sg;

/**
 * A {@link LinkedList collection} of {@link Explanation explanations}
 * associated with a specific {@link Node node}. Every node in this collection
 * has the associated node as its {@link Explanation#node node}.
 * 
 * @author Stephen G. Ware
 */
public class NodeExplanationList extends LinkedList<Explanation> {
	
	/**
	 * Creates a new empty list of explanations.
	 */
	protected NodeExplanationList() {
		// default constructor
	}
	
	@Override
	public String toString() {
		return "[Explanation List: " + size() + " explanations]";
	}
	
	@Override
	Explanation getNext(Explanation explanation) {
		return explanation.next;
	}
	
	@Override
	void setNext(Explanation previous, Explanation next) {
		previous.next = next;
	}
	
	/**
	 * Returns the explanations from this collection that have the given {@link
	 * Character character} as their {@link Explanation#character character}.
	 * 
	 * @param character the character whose explanations are desired
	 * @return the explanations from this collection for that character
	 */
	public ExplanationSubset get(Character character) {
		return new ExplanationSubset(this, explanation -> Utilities.equals(explanation.character, character));
	}
	
	/**
	 * Returns the explanations from this collection that {@link
	 * Sequence#startsWith(Action...) start} with a given {@link Action action}.
	 * 
	 * @param action the action which will be the start of the explanations
	 * @return the explanations from this collection that start with that action
	 */
	public ExplanationSubset get(Action action) {
		return new ExplanationSubset(this, explanation -> explanation.startsWith(action));
	}
	
	/**
	 * Returns the explanations from this collection that have the given {@link
	 * Character character} as their {@link Explanation#character character} and
	 * {@link Sequence#startsWith(Action...) start} with a given {@link Action
	 * action}.
	 * 
	 * @param character the character whose explanations are desired
	 * @param action the action which will be the start of the explanations
	 * @return the explanations from this collection for that character starting
	 * with that action
	 */
	public ExplanationSubset get(Character character, Action action) {
		return new ExplanationSubset(this, explanation -> Utilities.equals(explanation.character, character) && explanation.startsWith(action));
	}
}