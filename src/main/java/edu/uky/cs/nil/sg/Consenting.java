package edu.uky.cs.nil.sg;

import java.util.Set;

/**
 * Provides methods to reason about which {@link Character characters} {@link
 * Action#consenting consent} to a single {@link Action action} or {@link Plan
 * sequence of actions}.
 * 
 * @author Stephen G. Ware
 */
public interface Consenting {
	
	/**
	 * Returns true if all action(s) have no {@link Action#consenting consenting
	 * characters}--that is, if this action or sequence of actions can be taken
	 * any time it is convenient for the story. A sequence of zero actions is
	 * considered an author sequence.
	 * 
	 * @return true if all of the action(s) have no consenting characters or if
	 * this is a sequence of zero actions
	 */
	public default boolean isAuthor() {
		return consenting().size() == 0;
	}
	
	/**
	 * Returns true if this {@link Action action} or at least one action in this
	 * sequence is taken by a {@link Character character} which is {@link
	 * Action#isPlayer() marked as a player}. Note it is possible for an action
	 * or sequence to be taken by both player and {@link #isNPC() non-player}
	 * characters. If this is a sequence of zero actions, this method returns
	 * false.
	 * 
	 * @return true if this action or at least one action in this sequence is
	 * taken by a player character
	 * @see #isPlayerOnly()
	 */
	public default boolean isPlayer() {
		for(Character consenting : consenting())
			if(consenting.isPlayer())
				return true;
		return false;
	}
	
	/**
	 * Returns true if this {@link Action action} or every action in this
	 * sequence is taken only by {@link Character characters} which are {@link
	 * Action#isPlayer() marked as a players}. If this is a sequence of zero
	 * actions, this method returns false.
	 * 
	 * @return true if this action or every action in this sequence is taken
	 * only by player characters
	 * @see #isPlayer()
	 */
	public default boolean isPlayerOnly() {
		return isPlayer() && !isNPC();
	}
	
	/**
	 * Returns true if this {@link Action action} or at least one action in this
	 * sequence is taken by a {@link Character character} which is not {@link
	 * Action#isPlayer() marked as a player} (i.e. is a non-player character, or
	 * NPC). Note it is possible for an action or sequence to be taken by both
	 * {@link #isPlayer() player} and non-player characters. If this is a
	 * sequence of zero actions, this method returns false.
	 * 
	 * @return true if this action or at least one action in this sequence is
	 * taken by a non-player character
	 * @see #isNPCOnly()
	 */
	public default boolean isNPC() {
		for(Character consenting : consenting())
			if(!consenting.isPlayer())
				return true;
		return false;
	}
	
	/**
	 * Returns true if this {@link Action action} or every action in this
	 * sequence is taken only by {@link Character characters} which are not
	 * {@link Action#isPlayer() marked as a players} (i.e. are non-player
	 * characters or NPCs). If this is a sequence of zero actions, this method
	 * returns false.
	 * 
	 * @return true if this action or every action in this sequence is taken
	 * only by non-player characters
	 * @see #isNPC()
	 */
	public default boolean isNPCOnly() {
		return isNPC() && !isPlayer();
	}
	
	/**
	 * Returns true if the given {@link Character character} {@link
	 * Action#consenting consents} to this action or to any action in this
	 * sequence of actions.
	 * 
	 * @param character a character
	 * @return true if the character consents to this action or any action in
	 * this sequence of actions
	 */
	public default boolean consents(Character character) {
		return consenting().contains(character);
	}
	
	/**
	 * Returns the {@link Set set} of {@link Character characters} who {@link
	 * Action#consenting consent} to this action or the set of all characters
	 * who consent to all of the actions in this sequence of actions.
	 * 
	 * @return the set of characters who take this action or this sequence of
	 * actions
	 */
	public Set<Character> consenting();
}