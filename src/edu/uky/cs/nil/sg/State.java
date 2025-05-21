package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * A state defines a {@link Value value} for each {@link Fluent fluent} and
 * a numeric utility value for a story and each {@link Character character}
 * in a {@link StoryGraph story graph}.
 * <p>
 * Every {@link Node node} has a state. States do not model character beliefs.
 * State objects are distinct from node objects because nodes which are not the
 * same (due to having different character beliefs) may still have the same
 * values for each fluent and character, allowing state object to be reused to
 * save memory.
 * <p>
 * New states are usually not created directly but as a result of adding new
 * nodes to the story graph.
 * 
 * @author Stephen G. Ware
 */
public class State extends Numbered implements Comparable<State> {
	
	/**
	 * Returns the index in the array of utility values for a given {@link
	 * Character character}. The author, represented by {@code null}, uses index
	 * 0. The index of every other character is their {@link Character#getID()
	 * ID number} plus one.
	 * 
	 * @param character the character whose index is desired
	 * @return the index of that character's utility in the utility value array
	 */
	static final int index(Character character) {
		if(character == null)
			return 0;
		else
			return character.getID() + 1;
	}
	
	/**
	 * The values for each fluent, where the value for a fluent is stored at the
	 * index of the {@link Fluent#getID() fluent's ID number}
	 */
	private Value[] values;
	
	/**
	 * The utility values of the author and each character, where the value for
	 * a character is stored at {@link #index(Character) the character's index}
	 */
	private double[] utilities;
	
	/**
	 * Constructs a new state with the given ID number, array of fluent values,
	 * and array of utility values.
	 * 
	 * @param id the unique sequential ID number of the state
	 * @param values an array of fluent values
	 * @param utilities an array of utility values
	 */
	protected State(long id, Value[] values, double[] utilities) {
		super(id);
		this.values = values;
		this.utilities = utilities;
	}
	
	@Override
	public String toString() {
		return "[State " + getID() + "]";
	}
	
	@Override
	public int compareTo(State other) {
		return Long.compare(this.getID(), other.getID());
	}
	
	/**
	 * Returns this state's {@link Value value} for a given {@link Fluent
	 * fluent}. If the fluent has no value in this state, perhaps because it was
	 * added to the story graph after this state was created, this method
	 * returns null.
	 * 
	 * @param fluent the fluent whose value is desired
	 * @return the value for the fluent in this state
	 */
	public Value getValue(Fluent fluent) {
		if(fluent.getID() < 0 || fluent.getID() >= values.length)
			return null;
		else
			return values[fluent.getID()];
	}
	
	/**
	 * Returns the author's utility, which is how desirable this state is for
	 * the story as a whole.
	 * 
	 * @return the author's utility
	 */
	public double getUtility() {
		return getUtility(null);
	}
	
	/**
	 * Returns the utility for the given {@link Character character}, which is
	 * how desirable that character considers this state to be. If the character
	 * given is {@code null}, this method returns the {@link #getUtility()
	 * author's utility}. If the character has no utility value in this state,
	 * perhaps because it was added to the story graph after this state was
	 * created, this method returns {@link Double#NaN}.
	 * 
	 * @param character the character whose utility is desired
	 * @return the character's utility
	 */
	public double getUtility(Character character) {
		int index = index(character);
		if(index < 0 || index >= utilities.length)
			return Double.NaN;
		else
			return utilities[index];
	}
	
	/**
	 * Sets the {@link #values array of values} for this state.
	 * 
	 * @param values an array of values
	 */
	void setValues(Value[] values) {
		this.values = values;
	}
	
	/**
	 * Sets the {@link #utilities array of utility values} for this state.
	 * 
	 * @param values an array of utility values
	 */
	void setUtilities(double[] utilities) {
		this.utilities = utilities;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If any fluent has a {@link NominalValue nominal value} whose ID number
	 * has been set to {@link Settings#PRUNED} those values will be replaced
	 * with {@code null}.
	 */
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		for(int i=0; i<values.length; i++)
			if(values[i] instanceof NominalValue nominal && nominal.getID() == Settings.PRUNED)
				values[i] = null;
	}
}