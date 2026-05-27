package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The {@link NumberedList list} of {@link State states} in a {@link StoryGraph
 * story graph}, which assign a {@link Value value} to each {@link Fluent
 * fluent} and a numeric utility values to each {@link Character character}.
 * States are typically not created directly by the user; they are created when
 * a {@link NodeList#add(Function, Function) node is created}. State objects
 * exist separately from nodes because they can sometimes be reused by nodes
 * which have the same fluent and utility values but are not the same because
 * they have different character beliefs.
 * 
 * @author Stephen G. Ware
 */
public class StateList extends NumberedList<State> {
	
	/**
	 * Constructs a new state list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's states belong
	 */
	protected StateList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.STATES;
	}
	
	@Override
	protected String getSingular() {
		return "state";
	}
	
	/**
	 * Creates a new state with a given array of {@link Fluent fluent} {@link
	 * Value values} and utility values. Fluent values are expected to match
	 * the {@link Fluent#getID() ID number} of the fluent--meaning the value for
	 * fluent 0 is in index 0, the value for fluent 1 is in index 1, etc. The
	 * first utility is expected to be the author's utility. The second utility
	 * value is expected to be the utility for the character with {@link
	 * Character#getID() ID number 0}, the third the utility value for character
	 * 1, etc.
	 * <p>
	 * This method does not check whether a state with the same fluent and
	 * utility values already exists; it always adds a new state.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param values an arry of fluent values
	 * @param utilities an array of utility values
	 * @return a newly created state that has been added to this list
	 * @throws IllegalArgumentException if any of the fluent values have been
	 * removed or were not created in this list's story graph
	 */
	protected State add(Value[] values, double[] utilities) {
		for(Value value : values)
			if(value instanceof NominalValue nominal)
				graph.values.require(nominal);
		State state = new State(size(), values, utilities);
		add(state);
		return state;
	}
	
	/**
	 * Creates a new state from a function that maps {@link Fluent fluents} to
	 * {@link Value values} and {@link Character characters} to utility values.
	 * The function that maps characters to utility values should return the
	 * author's utility when given {@code null} as its input. If the function
	 * that maps characters to utility values ever returns null, it will be
	 * treated as {@link Double#NaN}.
	 * <p>
	 * This method does not check whether a state with the same fluent and
	 * utility values already exists; it always adds a new state.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param values a function that maps fluents to values
	 * @param utilities a function that maps characters (and null for the
	 * author) to utility values
	 * @return a newly created state that has been added to this list
	 * @throws IllegalArgumentException if any of the fluent values have been
	 * removed or were not created in this list's story graph
	 * @see #add(Value[], double[])
	 */
	public State add(Function<? super Fluent, ? extends Object> values, Function<? super Character, ? extends Object> utilities) {
		Value[] v = new Value[graph.fluents.size()];
		for(Fluent fluent : graph.fluents)
			v[fluent.getID()] = toValue(values.apply(fluent));
		double[] u = new double[1 + graph.characters.size()];
		u[State.index(null)] = toDouble(utilities.apply(null));
		for(Character character : graph.characters)
			u[State.index(character)] = toDouble(utilities.apply(character));
		return add(v, u);
	}
	
	private final Value toValue(Object object) {
		if(object == null)
			return null;
		else if(object instanceof Value)
			return (Value) object;
		else if(object instanceof Number)
			return NumericValue.get(object);
		else
			return graph.values.require(object.toString());
	}
	
	private final double toDouble(Object object) {
		return NumericValue.get(object).value;
	}
	
	/**
	 * Creates a new state from a map of {@link Fluent fluents} to {@link Value
	 * values} and {@link Character characters} to utility values. The map of
	 * characters to utility values should return the author's utility for the
	 * key {@code null}. If the map of characters to utility values ever returns
	 * null, it will be treated as {@link Double#NaN}.
	 * <p>
	 * This method does not check whether a state with the same fluent and
	 * utility values already exists; it always adds a new state.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param values a maps of fluents to values
	 * @param utilities a map of characters (and null for the author) to utility
	 * values
	 * @return a newly created state that has been added to this list
	 * @throws IllegalArgumentException if any of the fluent values have been
	 * removed or were not created in this list's story graph
	 * @see #add(Value[], double[])
	 */
	public State add(Map<? super Fluent, ? extends Object> values, Map<? super Character, ? extends Object> utilities) {
		return add(f -> values.get(f), c -> utilities.get(c));
	}
	
	@Override
	public boolean remove(Predicate<? super State> predicate, Status status) {
		return removeAndPrune(
			predicate,
			status,
			graph.nodes,
			graph.edges,
			graph.explanations
		);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the number of {@link StoryGraph#fluents fluents} or {@link
	 * StoryGraph#characters} in a story graph have changed since any of the
	 * states in this list were created, those states will be modified to
	 * reflect the changes.
	 */
	@Override
	protected boolean prune(Predicate<Object> predicate, Status status) {
		status.set("Pruning " + getPlural(), size());
		int fluents = 0;
		for(Fluent fluent : graph.fluents)
			if(fluent.getID() != Settings.PRUNED)
				fluents++;
		int characters = 1;
		for(Character character : graph.characters)
			if(character.getID() != Settings.PRUNED)
				characters++;
		boolean modified = false;
		for(State state : this) {
			state.prune(predicate);
			if(state.getID() == Settings.PRUNED)
				modified = true;
			if(fluents != graph.fluents.size()) {
				Value[] values = new Value[fluents];
				int next = 0;
				for(Fluent fluent : graph.fluents)
					if(fluent.getID() != Settings.PRUNED)
						values[next++] = state.getValue(fluent);
				state.setValues(values);
			}
			if(characters != graph.characters.size()) {
				double[] utilities = new double[characters];
				utilities[0] = state.getUtility();
				int next = 1;
				for(Character character : graph.characters)
					if(character.getID() != Settings.PRUNED)
						utilities[next++] = state.getUtility(character);
				state.setUtilities(utilities);
			}
			status.increment();
		}
		status.setMessage("Pruned " + status.getCount() + " " + getPlural());
		return modified;
	}
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		super.read(reader, status);
		if(reader.setFile(getFileName())) {
			int columns = graph.fluents.size() + 1 + graph.characters.size();
			String[] line = reader.readNextLineAsCSV(columns);
			while(line != null) {
				Value[] values = new Value[graph.fluents.size()];
				for(int i=0; i<values.length; i++) {
					if(line[i] == null)
						values[i] = null;
					else if(line[i].contains("."))
						values[i] = NumericValue.get(line[i]);
					else if(line[i].equals(Double.toString(Double.NaN)))
						values[i] = NumericValue.get(Double.NaN);
					else if(line[i].equals(Double.toString(Double.POSITIVE_INFINITY)))
						values[i] = NumericValue.get(Double.POSITIVE_INFINITY);
					else if(line[i].equals(Double.toString(Double.NEGATIVE_INFINITY)))
						values[i] = NumericValue.get(Double.NEGATIVE_INFINITY);
					else
						values[i] = graph.values.get(Utilities.toInteger(line[i]));
				}
				double[] utilities = new double[1 + graph.characters.size()];
				for(int i=0; i<utilities.length; i++)
					utilities[i] = NumericValue.get(line[graph.fluents.size() + i]).value;
				add(values, utilities);
				status.increment();
				line = reader.readNextLineAsCSV(columns);
			}
		}
		status.setMessage("Read " + status.getCount() + " " + getPlural());
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		super.write(writer, status);
		Object[] line = new Object[graph.fluents.size() + 1 + graph.characters.size()];
		for(State state : this) {
			for(Fluent fluent : graph.fluents)
				line[fluent.getID()] = state.getValue(fluent);
			line[graph.fluents.size() + State.index(null)] = state.getUtility(null);
			for(Character character : graph.characters)
				line[graph.fluents.size() + State.index(character)] = state.getUtility(character);
			writer.writeNextLineAsCSV(line);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " " + getPlural());
	}
}