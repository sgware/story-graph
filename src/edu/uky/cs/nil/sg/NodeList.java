package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The {@link NumberedList list} of {@link Node nodes} in a {@link StoryGraph
 * story graph}, which represent specific moments in the story, including what
 * {@link TemporalEdge actions can happen} and what {@link EpistemicEdge
 * beliefs characters have} about the state.
 * 
 * @author Stephen G. Ware
 */
public class NodeList extends NumberedList<Node> {
	
	/**
	 * Constructs a new node list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's nodes belong
	 */
	protected NodeList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.NODES;
	}
	
	@Override
	protected String getSingular() {
		return "node";
	}
	
	/**
	 * Creates a new node with a given {@link State state} of {@link Fluent
	 * fluent} {@link Value values} and utility values and no edges. Temporal
	 * edges can be added by the graph's {@link EdgeCollection#temporal temporal
	 * edge list}. Epistemic edges can be added by the graph's {@link
	 * EdgeCollection#epistemic epistemic edge list}.
	 * <p>
	 * This method does not check whether a node with the same state already
	 * exists; it always adds a new state.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param state a state specifying the value of every fluent and the utility
	 * of every character
	 * @return a newly created node that has been added to this list
	 * @throws IllegalArgumentException if the state has been removed or was not
	 * created in this list's story graph
	 */
	public Node add(State state) {
		graph.states.require(state);
		Node node = new Node(size(), state);
		add(node);
		return node;
	}
	
	/**
	 * {@link #add(State) Creates} a new node whose {@link
	 * StateList#add(Function, Function) state} is defined by a function that
	 * maps {@link Fluent fluents} to {@link Value values} and {@link Character
	 * characters} to utility values. The function that maps characters to
	 * utility values should return the author's utility when given {@code null}
	 * as its input. If the function that maps characters to utility values ever
	 * returns null, it will be treated as {@link Double#NaN}.
	 * <p>
	 * This method does not check whether a node with the same state already
	 * exists; it always adds a new state.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param values a function that maps fluents to values
	 * @param utilities a function that maps characters (and null for the
	 * author) to utility values
	 * @return a newly created node that has been added to this list
	 * @throws IllegalArgumentException if any of the fluent values have been
	 * removed or were not created in this list's story graph
	 */
	public Node add(Function<? super Fluent, ? extends Object> values, Function<? super Character, ? extends Object> utilities) {
		return add(graph.states.add(values, utilities));
	}
	
	/**
	 * {@link #add(State) Creates} a new node whose {@link
	 * StateList#add(Map, Map) state} is defined by a map of {@link Fluent
	 * fluents} to {@link Value values} and {@link Character characters} to
	 * utility values. The map of characters to utility values should return the
	 * author's utility for the key {@code null}. If the map of characters to
	 * utility values ever returns null, it will be treated as {@link
	 * Double#NaN}.
	 * <p>
	 * This method does not check whether a node with the same state already
	 * exists; it always adds a new state.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param values a maps of fluents to values
	 * @param utilities a map of characters (and null for the author) to utility
	 * values
	 * @return a newly created node that has been added to this list
	 * @throws IllegalArgumentException if any of the fluent values have been
	 * removed or were not created in this list's story graph
	 */
	public Node add(Map<? super Fluent, ? extends Object> values, Map<? super Character, ? extends Object> utilities) {
		return add(graph.states.add(values, utilities));
	}
	
	@Override
	public void remove(Predicate<? super Node> predicate, Status status) {
		removeAndPrune(
			predicate,
			status,
			graph.edges.temporal,
			graph.edges.epistemic,
			graph.explanations
		);
	}
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		readNodes(reader, status);
		readComments(reader, status);
	}
	
	/**
	 * Reads the {@link #getFileName() file} for this collection and {@link
	 * #add(State) adds} a new node for each line.
	 * 
	 * @param reader a story graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading the file or if
	 * the file is not formatted correctly
	 */
	protected void readNodes(GraphReader reader, Status status) throws IOException {
		super.read(reader, status);
		if(reader.setFile(getFileName())) {
			String[] line = reader.readNextLineAsCSV(1);
			while(line != null) {
				add(graph.states.get(Utilities.toLong(line[0])));
				status.increment();
				line = reader.readNextLineAsCSV(1);
			}
		}
		status.setMessage("Read " + status.getCount() + " " + getPlural());
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		writeNodes(writer, status);
		writeComments(writer, status);
	}
	
	/**
	 * Writes the nodes in this collection to {@link #getFileName() file}.
	 * 
	 * @param writer a story graph writer
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing the file
	 */
	protected void writeNodes(GraphWriter writer, Status status) throws IOException {
		super.write(writer, status);
		Object[] line = new Object[1];
		for(Node node : this) {
			line[0] = node.getState();
			writer.writeNextLineAsCSV(line);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " " + getPlural());
	}
}