package edu.uky.cs.nil.sg;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An action is a {@link StoryGraph story graph} {@link Symbol symbol} that
 * represents an event in a story. Actions have a (possibly empty) {@link
 * #consenting set of consenting} {@link Character characters} who are the
 * agents responsible for taking the action. When an action occurs, it
 * typically changes the state by changing which {@link Value values} are
 * assigned to the state's {@link Fluent fluents} and by modifying the {@link
 * Node#getBeliefs(Character) beliefs} of some characters.
 * <p>
 * New actions should be created in a story graph using the {@link
 * ActionList#add(String)} method.
 * <p>
 * Actions are the labels of {@link TemporalEdge temporal edges}.
 * 
 * @author Stephen G. Ware
 */
public class Action extends Symbol implements Comparable<Action>, Consenting {
	
	/**
	 * The set of {@link Character characters} who are responsible for taking
	 * this action. Not all characters involved in the action are necessarily
	 * consenting characters. This set is immutable, but it can be modified by
	 * the {@link ActionList#add(Action, Character)} and {@link
	 * ActionList#remove(Action, Character)} methods.
	 */
	public final Set<Character> consenting;
	
	/** The modifiable set of {@link #consenting consenting characters} */
	private final LinkedHashSet<Character> modifiable = new LinkedHashSet<>();
	
	/**
	 * Constructs a new action symbol with the given ID and name.
	 * 
	 * @param id the action's unique sequential ID number
	 * @param name the action's unique name
	 */
	protected Action(int id, String name) {
		super(id, name);
		this.consenting = Collections.unmodifiableSet(modifiable);
	}
	
	@Override
	public int compareTo(Action other) {
		return Integer.compare(this.getID(), other.getID());
	}
	
	@Override
	public Set<Character> consenting() {
		return consenting;
	}
	
	/**
	 * Adds a character to the set of {@link #consenting consenting characters}.
	 * 
	 * @param consenting the character to add
	 * @return true if this character was not already a consenting character and
	 * has been added, or false if the character was already a consenting
	 * character
	 */
	boolean add(Character consenting) {
		return modifiable.add(consenting);
	}
	
	/**
	 * Removes a character to the set of {@link #consenting consenting
	 * characters}.
	 * 
	 * @param consenting the character to remove
	 * @return true if this character was a consenting character and has been
	 * removed, or false if this character was not a consenting character
	 */
	boolean remove(Character consenting) {
		return modifiable.remove(consenting);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method removes any {@link #consenting consenting characters} whose
	 * ID numbers are set to {@link Settings#PRUNED} from the action.
	 */
	@Override
	protected void prune(Predicate<Object> predicate) {
		super.prune(predicate);
		modifiable.removeIf(consenting -> consenting.getID() == Settings.PRUNED);
	}
}