package edu.uky.cs.nil.sg;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A {@link Plan plan} that stores a sequence of {@link Action actions} in an
 * array.
 * 
 * @author Stephen G. Ware
 */
public class ArrayPlan extends Plan {
	
	/** The array of actions */
	private Action[] actions;
	
	/**
	 * Constructs a new array plan from an array of actions.
	 * 
	 * @param id the plan's unique sequential ID number 
	 * @param actions an array of actions
	 */
	protected ArrayPlan(long id, Action[] actions) {
		super(id);
		this.actions = actions;
	}
	
	@Override
	public Set<Character> consenting() {
		LinkedHashSet<Character> consenting = new LinkedHashSet<>();
		for(int i = actions.length - 1; i >= 0; i--)
			consenting.addAll(actions[i].consenting);
		return Collections.unmodifiableSet(consenting);
	}
	
	@Override
	public int size() {
		return actions.length;
	}
	
	@Override
	public Action get(int index) {
		if(index < 0 || index >= size())
			throw Exceptions.indexOutOfBounds(index, size());
		else
			return actions[index];
	}
	
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		this.actions = prune(actions, 0, 0);
	}
	
	private static final Action[] prune(Action[] actions, int index, int size) {
		if(index == actions.length) {
			if(size == actions.length)
				return actions;
			else
				return new Action[size];
		}
		else if(actions[index].getID() == Settings.PRUNED)
			return prune(actions, index + 1, size);
		else {
			Action[] pruned = prune(actions, index + 1, size + 1);
			pruned[size] = actions[index];
			return pruned;
		}
	}
}