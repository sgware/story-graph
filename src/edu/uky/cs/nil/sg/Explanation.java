package edu.uky.cs.nil.sg;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An explanation is a {@link #getPlan() plan} that a {@link #character
 * character} wants to take at a specific {@link #node node} in a {@link
 * StoryGraph story graph}.
 * <p>
 * If the {@link #character character} for an explanation is {@code null}, it
 * represents a plan formed by the story author.
 * 
 * @author Stephen G. Ware
 */
public class Explanation extends Numbered implements Iterable<Action>, Commented, Sequence {
	
	/** The node at which the plan begins */
	public final Node node;
	
	/** The character who wants to take the sequence of actions */
	public final Character character;
	
	/** The sequence of actions the character want to take */
	private Plan plan;
	
	/** The next explanation in the node's {@link NodeExplanationList} */
	Explanation next = null;
	
	/** The comment associated with this explanation */
	private String comment = null;
	
	/**
	 * Constructs a new explanation for a node, character, and plan.
	 * 
	 * @param id the explanation's unique sequential ID number
	 * @param node the node at which the plan begins
	 * @param character the character who wants to take the sequence of actions
	 * @param plan the sequence of actions the character wants to take
	 */
	protected Explanation(long id, Node node, Character character, Plan plan) {
		super(id);
		this.node = node;
		this.character = character;
		this.plan = plan;
	}
	
	@Override
	public String toString() {
		String string = "[Explanation " + getID();
		if(character != null)
			string += " for " + character;
		if(size() > 0)
			string += ":";
		for(Action action : this)
			string += " " + action;
		return string + "]";
	}
	
	@Override
	public Iterator<Action> iterator() {
		return getPlan().iterator();
	}
	
	@Override
	public String getComment() {
		return comment;
	}
	
	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public Set<Character> consenting() {
		return getPlan().consenting();
	}
	
	@Override
	public int size() {
		return getPlan().size();
	}
	
	@Override
	public Action get(int index) {
		return getPlan().get(index);
	}
	
	/**
	 * Returns the explanation's {@link Plan plan}, which is the sequence of
	 * actions this explanation's {@link #character character} wants to take.
	 * 
	 * @return the character's plan
	 */
	public Plan getPlan() {
		return plan;
	}
	
	/**
	 * Sets this explanation's plan.
	 * 
	 * @param plan the new plan for this explanation
	 */
	void setPlan(Plan plan) {
		this.plan = plan;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * An explanation will be pruned if its node, character, or plan have their
	 * ID numbers set to {@link Settings#PRUNED}.
	 */
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		if(node.getID() == Settings.PRUNED || (character != null && character.getID() == Settings.PRUNED) || plan.getID() == Settings.PRUNED)
			setID(Settings.PRUNED);
		if(getID() == Settings.PRUNED)
			node.explanations.remove(this);
	}
}