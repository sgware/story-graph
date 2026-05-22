package edu.uky.cs.nil.sg;

import java.io.IOException;

/**
 * The {@link EdgeList list} of {@link EpistemicEdge epistemic edges} in a
 * {@link StoryGraph story graph}, which represent each {@link Character
 * character's} beliefs about the current state of a story.
 * 
 * @author Stephen G. Ware
 */
public class EpistemicEdgeList extends EdgeList<EpistemicEdge> {
	
	/**
	 * Constructs a new epistemic edge list for a given story graph.
	 * 
	 * @param graph the story graph to which this list's epistemic edges belong
	 */
	protected EpistemicEdgeList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.EPISTEMIC;
	}
	
	@Override
	protected String getSingular() {
		return "epistemic edge";
	}
	
	@Override
	protected String getFileName() {
		return "epistemic.csv";
	}
	
	@Override
	protected String getCommentFileName() {
		return "epistemic_comments.txt";
	}
	
	/**
	 * Creates a new epistemic edge that extends from a tail {@link Node node}
	 * to a head node and is labeled with an {@link Character character}.
	 * <p>
	 * If an epistemic edge already exists from the tail node for this character
	 * and a different head node is given, this method throws an exception. If
	 * the same head node is given, the existing edge is returned.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * {@link MetaData#MODIFIED last modified} timestamp.
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
	 */
	public EpistemicEdge add(Node actual, Character character, Node beliefs) {
		graph.nodes.require(actual);
		graph.characters.require(character);
		graph.nodes.require(beliefs);
		EpistemicEdge edge = actual.edges.epistemic.out.get(character);
		if(edge == null) {
			edge = new EpistemicEdge(size(), actual, character, beliefs);
			add(edge);
			actual.edges.epistemic.out.add(edge);
			beliefs.edges.epistemic.in.add(edge);
		}
		else if(edge.head != beliefs)
			throw Exceptions.duplicateEdge(getSingular(), actual, character);
		return edge;
	}
	
	@Override
	protected void readEdge(Node tail, int label, Node head) throws IOException {
		add(tail, graph.characters.get(label), head);
	}
}