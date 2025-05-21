package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * The {@link SymbolList list} of {@link Action actions}, events that can occur
 * in a {@link StoryGraph story graph} to change the current state of a story
 * from one {@link Node node} to another. An action's {@link Action#consenting
 * consenting character} can be {@link #add(Action, Character) added} and {@link 
 * #remove(Action, Character) removed} by this list.
 * 
 * @author Stephen G. Ware
 */
public class ActionList extends SymbolList<Action> {
	
	/**
	 * Constructs a new action list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's actions belong
	 */
	protected ActionList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.ACTIONS;
	}
	
	@Override
	protected String getSingular() {
		return "action";
	}
	
	@Override
	public Action add(String name) {
		Action action = get(name);
		if(action == null) {
			if(name == null)
				throw Exceptions.cannotBeNull(getSingular() + " name");
			action = new Action(size(), name);
			add(action);
		}
		return action;
	}
	
	/**
	 * Adds a {@link Character character} to an {@link Action action's} set of
	 * {@link Action#consenting consenting characters}.
	 * <p>
	 * If this method modified the set of consenting characters, it updates the
	 * {@link StoryGraph#meta story graph's meta-data} {@link MetaData#MODIFIED
	 * last modified} timestamp.
	 * 
	 * @param action the action whose consenting characters will be added to
	 * @param consenting the character to add to the set of consenting
	 * characters
	 * @return true if the character was not already in the set and has now been
	 * added, false otherwise
	 */
	public boolean add(Action action, Character consenting) {
		this.require(action);
		graph.characters.require(consenting);
		if(action.add(consenting)) {
			graph.meta.set(MetaData.MODIFIED, Instant.now());
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Removes a {@link Character character} from an {@link Action action's} set
	 * of {@link Action#consenting consenting characters}.
	 * <p>
	 * If this method modified the set of consenting characters, it updates the
	 * {@link StoryGraph#meta story graph's meta-data} {@link MetaData#MODIFIED
	 * last modified} timestamp.
	 * 
	 * @param action the action whose consenting characters will be removed from
	 * @param consenting the character to remove from the set of consenting
	 * characters
	 * @return true if the character was in the set and has now been removed,
	 * false otherwise
	 */
	public boolean remove(Action action, Character consenting) {
		this.require(action);
		graph.characters.require(consenting);
		if(action.remove(consenting)) {
			graph.meta.set(MetaData.MODIFIED, Instant.now());
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void remove(Predicate<? super Action> predicate, Status status) {
		removeAndPrune(
			predicate,
			status,
			graph.plans,
			graph.edges.temporal,
			graph.explanations
		);
	}
	
	/**
	 * The name of the file where the list of each action's consenting
	 * characters is stored
	 */
	private static final String CONSENT_FILE = "consent.csv";
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		readSymbols(reader, status);
		if(reader.setFile(CONSENT_FILE)) {
			status.set("Reading consenting characters", (long) size());
			for(Action action : this) {
				String[] line = reader.readNextLineAsCSV();
				if(line == null)
					break;
				for(String consenting : line)
					add(action, graph.characters.get(Utilities.toInteger(consenting)));
				status.increment();
			}
			status.setMessage("Read " + status.getCount() + " consenting characters");
		}
		readComments(reader, status);
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		writeSymbols(writer, status);
		status.set("Writing consenting characters", (long) size());
		writer.setFile(CONSENT_FILE);
		for(Action action : this) {
			Object[] line = action.consenting.toArray(new Object[action.consenting.size()]);
			writer.writeNextLineAsCSV(line);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " consenting characters");
		writeComments(writer, status);
	}
}