package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.util.RandomAccess;
import java.util.function.Predicate;

/**
 * A {@link StoryGraphCollection collection} of {@link StoryGraph story graph}
 * elements that are all of the same type and have sequential ID numbers
 * starting at 0.
 * 
 * @param <T> the type of story graph element in this collection
 * @author Stephen G. Ware
 */
public abstract class StoryGraphList<T> extends StoryGraphCollection<T> implements RandomAccess {
	
	/**
	 * Constructs a new story graph list for the given story graph.
	 * 
	 * @param graph the story graph to which this collection and its elements
	 * belongs
	 */
	StoryGraphList(StoryGraph graph) {
		super(graph);
	}
	
	/**
	 * Returns the {@link MetaData meta-data} key that stores the size of this
	 * collection.
	 * 
	 * @return a meta-data key
	 */
	protected abstract String getMetaDataKey();
	
	/**
	 * Returns the name of the file in which the element of this collection are
	 * stored when reading and writing a graph from file.
	 * 
	 * @return the name of the file that stores the elements of this collection
	 */
	protected abstract String getFileName();
	
	/**
	 * Returns the name of the file in which the {@link Commented comments}
	 * associated with the elements of this collection are stored when reading
	 * and writing a graph from file.
	 * <p>
	 * By default, this method appends {@code "_comments.txt"} to the result of
	 * {@link #getSingular()}.
	 * 
	 * @return the name of the file that stores the elements of this collection
	 */
	protected String getCommentFileName() {
		return getSingular() + "_comments.txt";
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method will also update the {@link StoryGraph#meta story graph's
	 * meta-data} with the new number of elements of {@link #getMetaDataKey()
	 * this type} and the {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param element the element to remove
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 */
	@Override
	public void remove(T element, Status status) {
		super.remove(element, status);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method will also update the {@link StoryGraph#meta story graph's
	 * meta-data} with the new number of elements of {@link #getMetaDataKey()
	 * this type} and the {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param predicate a predicate that specifies which elements should be
	 * removed
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 */
	@Override
	public void remove(Predicate<? super T> predicate, Status status) {
		super.remove(predicate, status);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * with the new number of elements of {@link #getMetaDataKey() this type}.
	 * 
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 */
	@Override
	protected abstract void renumber(Status status);
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		setStatus(status, "Reading " + getPlural());
	}
	
	/**
	 * Reads the {@link #getCommentFileName() comments file} and {@link
	 * Commented#setComment(String) sets the comment} for each element of this
	 * collection. This method casts the elements of this collection to {@link
	 * Commented}, so it should only be called if this cast is safe.
	 * 
	 * @param reader a graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading the file or if
	 * the file is not formatted correctly
	 */
	void readComments(GraphReader reader, Status status) throws IOException {
		if(reader.setFile(getCommentFileName())) {
			setStatus(status, "Reading " + getSingular() + " comments");
			for(T element : this) {
				String comment = reader.readNextLineAsString();
				if(comment == null)
					break;
				else if(!comment.isEmpty())
					((Commented) element).setComment(comment);
				status.increment();
			}
			status.setMessage("Read " + status.getCount() + " " + getSingular() + " comments");
		}
	}
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		setStatus(status, "Writing " + getPlural());
		writer.setFile(getFileName());
	}
	
	/**
	 * Writes the {@link Commented#getComment() comment} of each element in the
	 * collection to the {@link #getCommentFileName() comment file}. This method
	 * casts the elements of this collection to {@link Commented}, so it should
	 * only be called if this cast is safe.
	 * 
	 * @param writer a graph writer
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing the file
	 */
	void writeComments(GraphWriter writer, Status status) throws IOException {
		setStatus(status, "Writing " + getSingular() + " comments");
		writer.setFile(getCommentFileName());
		for(T element : this) {
			String comment = ((Commented) element).getComment();
			if(comment == null)
				writer.writeNextLineAsString("");
			else
				writer.writeNextLineAsString(comment);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " " + getSingular() + " comments");
	}
	
	/**
	 * {@link Status#setMessage(String) Sets the status message} of a given
	 * status object, and if the {@link StoryGraph#meta graph's meta-data} has
	 * {@link #getMetaDataKey() an entry for the size of this collection}, also
	 * {@link Status#setTotal(Long) sets the total}.
	 * 
	 * @param status the status object whose message and total will be set
	 * @param message the message
	 */
	private final void setStatus(Status status, String message) {
		status.setMessage(message);
		Long size = graph.meta.getLong(getMetaDataKey());
		if(size != null)
			status.setTotal(size);
	}
}