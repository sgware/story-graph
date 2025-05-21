package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * The {@link SymbolList list} of {@link Character characters}, or agents, who
 * take actions and have possibly wrong beliefs in a {@link StoryGraph story
 * graph}. Characters can be {@link #setPlayer(Character, boolean) player
 * characters} or non-player characters.
 * 
 * @author Stephen G. Ware
 */
public class CharacterList extends SymbolList<Character> {
	
	/**
	 * Constructs a new character list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's characters belong
	 */
	protected CharacterList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.CHARACTERS;
	}
	
	@Override
	protected String getSingular() {
		return "character";
	}
	
	@Override
	public Character add(String name) {
		Character character = get(name);
		if(character == null) {
			if(name == null)
				throw Exceptions.cannotBeNull(getSingular() + " name");
			character = new Character(size(), name);
			add(character);
		}
		return character;
	}
	
	/**
	 * Marks as {@link Character character} is a player character or a
	 * non-player character. Player characters are typically controlled by the
	 * person experiencing an interactive narrative, whereas non-player
	 * characters are typically controlled by the story author.
	 * <p>
	 * If this method modified the character, it updates the {@link
	 * StoryGraph#meta story graph's meta-data} {@link MetaData#MODIFIED last
	 * modified} timestamp.
	 * 
	 * @param character any character
	 * @param value true to set the character as a player character, or false to
	 * set the character as a non-player character
	 */
	public void setPlayer(Character character, boolean value) {
		this.require(character);
		if(character.isPlayer() != value) {
			character.setPlayer(value);
			graph.meta.set(MetaData.MODIFIED, Instant.now());
		}
	}
	
	@Override
	public void remove(Predicate<? super Character> predicate, Status status) {
		removeAndPrune(
			predicate,
			status,
			graph.actions,
			graph.states,
			graph.edges.epistemic,
			graph.explanations
		);
	}
	
	/** The name of the file where the list of player characters is stored */
	private static final String PLAYERS_FILE = "players.txt";
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		readSymbols(reader, status);
		if(reader.setFile(PLAYERS_FILE)) {
			status.setMessage("Reading player characters");
			String line = reader.readNextLineAsString();
			while(line != null) {
				setPlayer(require(line), true);
				status.increment();
				line = reader.readNextLineAsString();
			}
			status.setMessage("Read " + status.getCount() + " player characters");
		}
		readComments(reader, status);
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		writeSymbols(writer, status);
		status.setMessage("Writing player characters");
		writer.setFile(PLAYERS_FILE);
		for(Character character : this) {
			if(character.isPlayer()) {
				writer.writeNextLineAsString(character.name);
				status.increment();
			}
		}
		status.setMessage("Wrote " + status.getCount() + " player characters");
		writeComments(writer, status);
	}
}