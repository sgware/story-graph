package edu.uky.cs.nil.sg;

import java.io.IOException;

/**
 * The {@link NumberedList list} of {@link Explanation explanations} in a {@link
 * StoryGraph story graph}, which document the {@link Plan plans} that a {@link
 * Character character} wants to taken in a given {@link Node node}.
 * 
 * @author Stephen G. Ware
 */
public class ExplanationList extends NumberedList<Explanation> {
	
	/**
	 * Constructs a new explanation list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's explanations belong
	 */
	protected ExplanationList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.EXPLANATIONS;
	}
	
	@Override
	protected String getSingular() {
		return "explanation";
	}
	
	/**
	 * Creates a new explanation for a {@link Character character} that has
	 * formed a {@link Plan plan} at some {@link Node node} in a {@link
	 * StoryGraph story graph}. If the character given is {@code null}, the
	 * plan represents a plan for the story author.
	 * <p>
	 * This method does not check whether an explanation with the same node,
	 * character, and plan already exists; it always adds a new explanation.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param node the node at which the character forms a plan
	 * @param character the character forming the plan
	 * @param plan the plan the character forms
	 * @return a newly created explanation that has been added to this list
	 * @throws IllegalArgumentException if the node, character, or any of the
	 * actions in the plan have been removed or were not created in this list's
	 * story graph
	 */
	public Explanation add(Node node, Character character, Plan plan) {
		graph.nodes.require(node);
		if(character != null)
			graph.characters.require(character);
		graph.plans.require(plan);
		Explanation explanation = new Explanation(size(), node, character, plan);
		add(explanation);
		node.explanations.add(explanation);
		return explanation;
	}
	
	/**
	 * {@link #add(Node, Character, Plan) Creates} a new explanation when the
	 * story author ({@code null} character) has formed a {@link Plan plan} at
	 * some {@link Node node} in a {@link StoryGraph story graph}.
	 * 
	 * @param node the node at which the author forms a plan
	 * @param plan the plan the character forms
	 * @return a newly created explanation that has been added to this list
	 * @throws IllegalArgumentException if the node or any of the actions in the
	 * plan have been removed or were not created in this list's story graph
	 * @see #add(Node, Character, Plan)
	 */
	public Explanation add(Node node, Plan plan) {
		return add(node, null, plan);
	}
	
	/**
	 * {@link #add(Node, Character, Plan) Creates} a new explanation for a
	 * {@link Character character} that has formed a {@link Plan plan} from a
	 * sequence of {@link Action actions} at some {@link Node node} in a {@link
	 * StoryGraph story graph}. If the character given is {@code null}, the
	 * plan represents a plan for the story author.
	 * 
	 * @param node the node at which the character forms a plan
	 * @param character the character forming the plan
	 * @param plan a sequence of actions representing the plan the character
	 * forms
	 * @return a newly created explanation that has been added to this list
	 * @throws IllegalArgumentException if the node, character, or any of the
	 * actions in the plan have been removed or were not created in this list's
	 * story graph
	 * @see #add(Node, Character, Plan)
	 */
	public Explanation add(Node node, Character character, Action...plan) {
		return add(node, character, graph.plans.add(plan));
	}
	
	/**
	 * {@link #add(Node, Character, Plan) Creates} a new explanation when the
	 * story author ({@code null} character) has formed a {@link Plan plan} from
	 * a sequence of {@link Action actions} at some {@link Node node} in a
	 * {@link StoryGraph story graph}.
	 * 
	 * @param node the node at which the author forms a plan
	 * @param plan a sequence of actions representing the plan the character
	 * forms
	 * @return a newly created explanation that has been added to this list
	 * @throws IllegalArgumentException if the node or any of the actions in the
	 * plan have been removed or were not created in this list's story graph
	 * @see #add(Node, Character, Plan)
	 */
	public Explanation add(Node node, Action...plan) {
		return add(node, null, plan);
	}
	
	/**
	 * {@link #add(Node, Character, Plan) Creates} a new explanation for a
	 * {@link Character character} that has formed a {@link Plan plan} from an
	 * {@link Iterable sequence} of {@link Action actions} at some {@link Node
	 * node} in a {@link StoryGraph story graph}. If the character given is
	 * {@code null}, the plan represents a plan for the story author.
	 * 
	 * @param node the node at which the character forms a plan
	 * @param character the character forming the plan
	 * @param plan a sequence of actions representing the plan the character
	 * forms
	 * @return a newly created explanation that has been added to this list
	 * @throws IllegalArgumentException if the node, character, or any of the
	 * actions in the plan have been removed or were not created in this list's
	 * story graph
	 * @see #add(Node, Character, Plan)
	 */
	public Explanation add(Node node, Character character, Iterable<Action> plan) {
		return add(node, character, graph.plans.add(plan));
	}
	
	/**
	 * {@link #add(Node, Character, Plan) Creates} a new explanation when the
	 * story author ({@code null} character) has formed a {@link Plan plan} from
	 * a {@link Iterable sequence} of {@link Action actions} at some {@link Node
	 * node} in a {@link StoryGraph story graph}.
	 * 
	 * @param node the node at which the author forms a plan
	 * @param plan a sequence of actions representing the plan the character
	 * forms
	 * @return a newly created explanation that has been added to this list
	 * @throws IllegalArgumentException if the node or any of the actions in the
	 * plan have been removed or were not created in this list's story graph
	 * @see #add(Node, Character, Plan)
	 */
	public Explanation add(Node node, Iterable<Action> plan) {
		return add(node, null, plan);
	}
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		readExplanations(reader, status);
		readComments(reader, status);
	}
	
	/**
	 * Reads the {@link #getFileName() file} for this collection and {@link
	 * #add(Node, Character, Plan) adds} a new explanation for each line.
	 * 
	 * @param reader a story graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading the file or if
	 * the file is not formatted correctly
	 */
	protected void readExplanations(GraphReader reader, Status status) throws IOException {
		super.read(reader, status);
		if(reader.setFile(getFileName())) {
			String[] line = reader.readNextLineAsCSV(3);
			while(line != null) {
				Node node = graph.nodes.get(Utilities.toLong(line[0]));
				Character character = line[1] == null ? null : graph.characters.get(Utilities.toInteger(line[1]));
				Plan plan = graph.plans.get(Utilities.toLong(line[2]));
				add(node, character, plan);
				status.increment();
				line = reader.readNextLineAsCSV(3);
			}
		}
		status.setMessage("Read " + status.getCount() + " " + getPlural());
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		writeExplanations(writer, status);
		writeComments(writer, status);
	}
	
	/**
	 * Writes the explanations in this collection to {@link #getFileName()
	 * file}.
	 * 
	 * @param writer a story graph writer
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing the file
	 */
	protected void writeExplanations(GraphWriter writer, Status status) throws IOException {
		super.write(writer, status);
		Object[] line = new Object[3];
		for(Explanation explanation : this) {
			line[0] = explanation.node;
			line[1] = explanation.character;
			line[2] = explanation.getPlan();
			writer.writeNextLineAsCSV(line);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " " + getPlural());
	}
}