package edu.uky.cs.nil.sg;

/**
 * A character is a {@link StoryGraph story graph} {@link Symbol symbol} that
 * represents one of the agents in a story. A character can {@link
 * Action#consenting consent to take actions}, has (possibly wrong) {@link
 * Node#getBeliefs(Character) beliefs} about the current state, and has a {@link
 * Node#getUtility(Character) utility} that represents how desirable they find
 * the current state. A character can optionally be marked as {@link #isPlayer()
 * a player character}, meaning their actions are typically chosen by a person
 * interacting with the story.
 * <p>
 * New characters should be created in a story graph using the {@link
 * CharacterList#add(String)} method.
 * <p>
 * Characters are the labels of {@link EpistemicEdge epistemic edges}.
 * 
 * @author Stephen G. Ware
 */
public class Character extends Symbol implements Comparable<Character> {
	
	/** Whether this is a player characters */
	private boolean player = false;
	
	/**
	 * Constructs a new character symbol with the given ID and name.
	 * 
	 * @param id the character's unique sequential ID number
	 * @param name the character's unique name
	 */
	protected Character(int id, String name) {
		super(id, name);
	}
	
	@Override
	public int compareTo(Character other) {
		return Integer.compare(this.getID(), other.getID());
	}
	
	/**
	 * Returns whether this character is marked as a player character. This
	 * value can be changed using the {@link
	 * CharacterList#setPlayer(Character, boolean)} method.
	 * 
	 * @return true if this is a player character, false otherwise
	 */
	public boolean isPlayer() {
		return player;
	}
	
	/**
	 * Sets whether this character is marked as a player character.
	 * 
	 * @param value true if this shold be a player character, false otherwise
	 */
	void setPlayer(boolean value) {
		this.player = value;
	}
}