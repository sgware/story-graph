package edu.uky.cs.nil.sg;

/**
 * A sequence of {@link Action actions}, such as a {@link Plan plan} or {@link
 * Explanation explanation}.
 * 
 * @author Stephen G. Ware
 */
public interface Sequence extends Iterable<Action>, Consenting {
	
	@Override
	public default boolean isAuthor() {
		for(Action action : this)
			if(!action.isAuthor())
				return false;
		return true;
	}
	
	@Override
	public default boolean isPlayer() {
		for(Action action : this)
			if(action.isPlayer())
				return true;
		return false;
	}
	
	@Override
	public default boolean isPlayerOnly() {
		for(Action action : this)
			if(!action.isPlayerOnly())
				return false;
		return size() > 0;
	}
	
	@Override
	public default boolean isNPC() {
		for(Action action : this)
			if(action.isNPC())
				return true;
		return false;
	}
	
	@Override
	public default boolean isNPCOnly() {
		for(Action action : this)
			if(!action.isNPCOnly())
				return false;
		return size() > 0;
	}
	
	@Override
	public default boolean consents(Character character) {
		for(Action action : this)
			if(action.consents(character))
				return true;
		return false;
	}
	
	/**
	 * Returns the number of {@link Action actions} in the sequence.
	 * 
	 * @return the number of actions
	 */
	public abstract int size();
	
	/**
	 * Returns the {@link Action action} at the given index in the sequence. The
	 * first action has an index of 0, the second an index of 1, etc.
	 * 
	 * @param index the index of the desired action
	 * @return the action at that index
	 * @throws IndexOutOfBoundsException if the index does not exist
	 */
	public abstract Action get(int index);
	
	/**
	 * Returns true if this sequence contains the given action sequence as a
	 * subsequence. The subsequence does not need to be contiguous, but it does
	 * need to appear in order. The sequence {@code A C} appears in {@code A B
	 * C} as a subsequence, but the sequence {@code B A} does not.
	 * 
	 * @param subsequence any sequence of actions
	 * @return true if the given sequence appears as a subsequence in this
	 * sequence, false otherwise
	 */
	public default boolean contains(Action...subsequence) {
		int index = 0;
		for(Action action : this)
			if(index < subsequence.length && Utilities.equals(action, subsequence[index]))
				index++;
		return index == subsequence.length;
	}
	
	/**
	 * Returns true if this sequence contains the given action sequence as a
	 * subsequence.
	 * 
	 * @param subsequence any sequence of actions
	 * @return true if the given sequence appears as a subsequence in this
	 * sequence, false otherwise
	 * @see #contains(Action...)
	 */
	public default boolean contains(Iterable<Action> subsequence) {
		return contains(Utilities.toArray(subsequence, Action.class));
	}
	
	/**
	 * Returns true if this sequence starts with exactly the the given sequence
	 * of actions.
	 * 
	 * @param plan any sequence of actions
	 * @return true if the first actions in this sequence are the same as the
	 * given sequence
	 */
	public default boolean startsWith(Action...plan) {
		if(plan.length > size())
			return false;
		for(int i = 0; i < plan.length; i++)
			if(!Utilities.equals(get(i), plan[i]))
				return false;
		return true;
	}
	
	/**
	 * Returns true if this sequence starts with exactly the the given sequence
	 * of actions.
	 * 
	 * @param plan any sequence of actions
	 * @return true if the first actions in this sequence are the same as the
	 * given sequence
	 * @see #startsWith(Action...)
	 */
	public default boolean startsWith(Iterable<Action> plan) {
		return startsWith(Utilities.toArray(plan, Action.class));
	}
	
	/**
	 * Returns true if this sequence ends with exactly the the given sequence of
	 * actions.
	 * 
	 * @param plan any sequence of actions
	 * @return true if the last actions in this sequence are the same as the
	 * given sequence
	 */
	public default boolean endsWith(Action...plan) {
		int offset = size() - plan.length;
		if(offset < 0)
			return false;
		for(int i = 0; i < plan.length; i++)
			if(!Utilities.equals(get(offset + i), plan[i]))
				return false;
		return true;
	}
	
	/**
	 * Returns true if this sequence ends with exactly the the given sequence of
	 * actions.
	 * 
	 * @param plan any sequence of actions
	 * @return true if the last actions in this sequence are the same as the
	 * given sequence
	 * @see #endsWith(Action...)
	 */
	public default boolean endsWith(Iterable<Action> plan) {
		return endsWith(Utilities.toArray(plan, Action.class));
	}
}