package edu.uky.cs.nil.sg;

import java.io.IOException;

/**
 * A {@link NumberedList collection} of {@link Edge edges} of the same type.
 * 
 * @param <E> the type of edge in this collection
 * @author Stephen G. Ware
 */
public abstract class EdgeList<E extends Edge> extends NumberedList<E> {
	
	/**
	 * Constructs a new edge list. If the {@link StoryGraph#meta story graph's
	 * meta-data} specifies the number of elements in the list, it will be
	 * initialized with exactly that capacity.
	 * 
	 * @param graph the story graph to which the edges belong
	 */
	protected EdgeList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected abstract String getFileName();
	
	@Override
	protected abstract String getCommentFileName();
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		readEdges(reader, status);
		readComments(reader, status);
	}
	
	/**
	 * Reads the {@link #getFileName() file} for this collection and {@link
	 * #readEdge(Node, int, Node) adds} a new edge for each line.
	 * 
	 * @param reader a story graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading the file or if
	 * the file is not formatted correctly
	 */
	protected void readEdges(GraphReader reader, Status status) throws IOException {
		super.read(reader, status);
		if(reader.setFile(getFileName())) {
			String[] line = reader.readNextLineAsCSV(3);
			while(line != null) {
				Node tail = graph.nodes.get(Utilities.toLong(line[0]));
				int label = Utilities.toInteger(line[1]);
				Node head = graph.nodes.get(Utilities.toLong(line[2]));
				readEdge(tail, label, head);
				status.increment();
				line = reader.readNextLineAsCSV(3);
			}
		}
		status.setMessage("Read " + status.getCount() + " " + getPlural());
	}
	
	/**
	 * While {@link #readEdges(GraphReader, Status) reading} this collection
	 * from file, this method is used to create each edge based on the the tail
	 * node, ID number of the label, and head node.
	 * 
	 * @param tail the tail node of the edge
	 * @param label the ID number of the label of the edge
	 * @param head the head node of the edge
	 * @throws IOException if the edge is not formatted correctly
	 */
	protected abstract void readEdge(Node tail, int label, Node head) throws IOException;
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		writeEdges(writer, status);
		writeComments(writer, status);
	}
	
	/**
	 * Writes the edges in this collection to {@link #getFileName() file}.
	 * 
	 * @param writer a story graph writer
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing the file
	 */
	protected void writeEdges(GraphWriter writer, Status status) throws IOException {
		super.write(writer, status);
		Object[] line = new Object[3];
		for(Edge edge : this) {
			line[0] = edge.tail;
			line[1] = edge.label;
			line[2] = edge.head;
			writer.writeNextLineAsCSV(line);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " " + getPlural());
	}
}