package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * A plan is a specific {@link Sequence sequence} of {@link Action actions}.
 * <p>
 * Every {@link Explanation explanation} has a plan. Plan objects are distinct
 * from explanation objects because explanations which are not the same (because
 * they are associated with different nodes or different characters) may still
 * have the same plan, allowing plan object to be reused to save memory.
 * <p>
 * New plans are usually not created directly but as a result of adding new
 * explanations to the graph.
 * 
 * @author Stephen G. Ware
 */
public abstract class Plan extends Numbered implements Iterable<Action>, Comparable<Plan>, Sequence {
	
	/**
	 * Constructs a new plan with the given ID number.
	 * 
	 * @param id the unique sequential ID number of the plan
	 */
	protected Plan(long id) {
		super(id);
	}
	
	@Override
	public String toString() {
		String string = "[Plan " + getID();
		if(size() > 0)
			string += ":";
		for(Action action : this)
			string += " " + action;
		return string + "]";
	}
	
	@Override
	public int compareTo(Plan other) {
		return Long.compare(this.getID(), other.getID());
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method removes any {@link Action actions} from the plan whose {@link
	 * Action#getID() ID numbers} have been set to {@link Settings#PRUNED}.
	 */
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
	}
}