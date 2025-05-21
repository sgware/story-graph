package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Predicate;


/**
 * A {@link StoryGraphCollection collection} of all {@link Edge edges} in a
 * {@link StoryGraph story graph}.
 * 
 * @author Stephen G. Ware
 */
public class EdgeCollection extends StoryGraphCollection<Edge> implements EdgeIterable<Edge> {
	
	/** The story graph's temporal edges */
	public final TemporalEdgeList temporal;
	
	/** The story graph's epistemic edges */
	public final EpistemicEdgeList epistemic;
	
	/**
	 * Constructs a new edge collection for the given story graph.
	 * 
	 * @param graph the story graph to which the edges belong
	 */
	protected EdgeCollection(StoryGraph graph) {
		super(graph);
		this.temporal = new TemporalEdgeList(graph);
		this.epistemic = new EpistemicEdgeList(graph);
	}
	
	@Override
	public String toString() {
		return "[Edge Collection: " + temporal.size() + " " + temporal.getPlural() + "; " + epistemic.size() + " " + epistemic.getPlural() + "]";
	}
	
	@Override
	public Iterator<Edge> iterator() {
		return new MergeIterator<>(temporal.iterator(), epistemic.iterator());
	}
	
	@Override
	protected String getSingular() {
		return "edge";
	}
	
	/**
	 * Returns the total number of edges in the story graph.
	 * 
	 * @return the number of edges
	 */
	public long size() {
		return temporal.size() + epistemic.size();
	}
	
	@Override
	public Edge get(Node tail, Object label, Node head) {
		return tail.edges.get(tail, label, head);
	}
	
	/**
	 * {@link TemporalEdgeList#add(Node, Action, Node) Adds a temporal edge} to
	 * the story graph.
	 * 
	 * @param before the tail node, representing the story state before the
	 * action
	 * @param action the action that can be taken
	 * @param after the head node, representing the story state after the action
	 * is taken
	 * @return a new temporal edge with the given tail, label, and head, or the
	 * existing edge if that exact edge already exists
	 * @throws IllegalArgumentException if an edge already exists from the tail
	 * node for this action but not to the given head node
	 * @throws IllegalArgumentException if the tail node, action, or head node
	 * have been removed or were not created in this list's story graph
	 * @see TemporalEdgeList#add(Node, Action, Node)
	 */
	public TemporalEdge add(Node before, Action action, Node after) {
		return temporal.add(before, action, after);
	}
	
	/**
	 * {@link EpistemicEdgeList#add(Node, Character, Node) Adds an epistemic
	 * edge} to the story graph.
	 * 
	 * @param actual the tail node, representing the state the story is actually
	 * in
	 * @param character the character that has beliefs
	 * @param beliefs the head node, representing the story state the character
	 * beliefs to be the case
	 * @return a new epistemic edge with the given tail, label, and head, or the
	 * existing edge if that exact edge already exists
	 * @throws IllegalArgumentException if an edge already exists from the tail
	 * node for this character but not to the given head node
	 * @throws IllegalArgumentException if the tail node, character, or head
	 * node have been removed or were not created in this list's story graph
	 * @see EpistemicEdgeList#add(Node, Character, Node)
	 */
	public EpistemicEdge add(Node actual, Character character, Node beliefs) {
		return epistemic.add(actual, character, beliefs);
	}
	
	@Override
	protected boolean validate(Object object) {
		return temporal.validate(object) || epistemic.validate(object);
	}
	
	@Override
	protected boolean prune(Predicate<Object> predicate, Status status) {
		return temporal.prune(predicate, status) || epistemic.prune(predicate, status);
	}
	
	@Override
	protected void renumber(Status status) {
		temporal.renumber(status);
		epistemic.renumber(status);
	}
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		temporal.read(reader, status);
		epistemic.read(reader, status);
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		temporal.write(writer, status);
		epistemic.write(writer, status);
	}
}