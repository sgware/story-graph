package edu.uky.cs.nil.sg;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A {@link Plan plan} formed by prepending a single {@link Action action} to
 * an existing plan (i.e. by adding a single action to the tail).
 * 
 * @author Stephen G. Ware
 */
public class TailPlan extends Plan {
	
	/** The action prepended to the existing plan */
	private Action first;
	
	/** The existing plan to which the action is prepended */
	final Plan rest;
	
	/**
	 * Constructs a new tail plan from a first action and an existing plan.
	 * 
	 * @param id the plan's unique sequential ID number
	 * @param first the first action in the plan
	 * @param rest the rest of the plan
	 */
	protected TailPlan(long id, Action first, Plan rest) {
		super(id);
		this.first = first;
		this.rest = rest;
	}
	
	@Override
	public Set<Character> consenting() {
		LinkedHashSet<Character> consenting = new LinkedHashSet<>();
		consenting.addAll(rest.consenting());
		if(first != null)
			for(Character character : first.consenting)
				consenting.add(character);
		return Collections.unmodifiableSet(consenting);
	}
	
	@Override
	public int size() {
		if(first == null)
			return rest.size();
		else
			return 1 + rest.size();
	}
	
	@Override
	public Action get(int index) {
		if(index < 0 || index >= size())
			throw Exceptions.indexOutOfBounds(index, size());
		else if(first == null)
			return rest.get(index);
		else if(index == 0)
			return first;
		else
			return rest.get(index - 1);
	}
	
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		if(first.getID() == Settings.PRUNED)
			first = null;
	}
}