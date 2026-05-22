package edu.uky.cs.nil.sg;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * A story graph represents moments in a story as {@link Node nodes}, how {@link
 * Action actions} can change the state of the story world ({@link TemporalEdge
 * temporal edges}), what each {@link Character character} believes about the
 * state ({@link EpistemicEdge epistemic edges}), and what each character {@link
 * Plan plans} to do ({@link Explanation explanations}).
 * 
 * @author Stephen G. Ware
 */
public class StoryGraph {
	
	/**
	 * A collection of key/value pairs that record important information about
	 * the story graph
	 */
	public final MetaData meta;
	
	/**
	 * The {@link Character characters}, or agents, who have beliefs, take
	 * {@link Action actions}, and form {@link Plan plans}
	 */
	public final CharacterList characters;
	
	/**
	 * The {@link Fluent fluents}, or state variables, whose {@link Value values}
	 * define the current {@link State state} of a story world {@link Node node}
	 */
	public final FluentList fluents;
	
	/**
	 * The {@link NominalValue nominal values} that are defined in this story
	 * graph and can be assigned to {@link Fluent fluents} in {@link Node nodes}
	 */
	public final ValueList values;
	
	/**
	 * The {@link State state} objects that record which {@link Value values}
	 * each {@link Fluent fluent} has and the utility values for the story
	 * author and each {@link Character character}
	 */
	protected final StateList states;
	
	/**
	 * The {@link Action actions} that can occur in the story to change the
	 * state from one {@link Node node} to another
	 */
	public final ActionList actions;
	
	/** The {@link Plan plans} that {@link Character characters} may form */
	protected final PlanList plans;
	
	/**
	 * The {@link Node nodes} of the story graph, which represent specific
	 * moments during the story, including which {@link Action actions} can
	 * happen and what each {@link Character character} believes about the state
	 */
	public final NodeList nodes;
	
	/**
	 * All {@link Edge edges} of the story graph, which are divided into two
	 * types: {@link TemporalEdge temporal edges} that represent {@link Action
	 * actions} occurring and {@link EpistemicEdge epistemic edges} that
	 * represent {@link Character character} beliefs
	 */
	public final EdgeCollection edges;
	
	/**
	 * The {@link Plan plans} formed by the story author and the story's {@link
	 * Character characters} that what they want to do at each {@link Node node}
	 */
	public final ExplanationList explanations;
	
	/**
	 * Constructs a new story graph from a given meta-data table. The meta-data
	 * records the numbers of elements in each of the graph's lists, allowing
	 * them to be allocated with exactly enough space to hold their elements.
	 * 
	 * @param meta the meta-data table
	 */
	protected StoryGraph(MetaData meta) {
		this.meta = meta;
		this.characters = new CharacterList(this);
		this.fluents = new FluentList(this);
		this.values = new ValueList(this);
		this.states = new StateList(this);
		this.actions = new ActionList(this);
		this.plans = new PlanList(this);
		this.nodes = new NodeList(this);
		this.edges = new EdgeCollection(this);
		this.explanations = new ExplanationList(this);
	}
	
	/**
	 * Constructs a new, empty story graph.
	 */
	public StoryGraph() {
		this(new MetaData());
	}
	
	@Override
	public String toString() {
		String string = "[Story Graph";
		String title = meta.getString(MetaData.TITLE);
		if(title != null)
			string += " \"" + title + "\"";
		string += ": " + characters.size() + " " + characters.getPlural();
		string += "; " + fluents.size() + " " + fluents.getPlural();
		string += "; " + values.size() + " " + values.getPlural();
		string += "; " + actions.size() + " " + actions.getPlural();
		string += "; " + nodes.size() + " " + nodes.getPlural();
		string += "; " + edges.temporal.size() + " " + edges.temporal.getPlural();
		string += "; " + edges.epistemic.size() + " " + edges.epistemic.getPlural();
		string += "; " + explanations.size() + " " + explanations.getPlural();
		string += "]";
		return string;
	}
	
	/**
	 * Returns the {@link MetaData#VERSION version} of the story graph library
	 * that was used to create this story graph as a string.
	 * 
	 * @return a string representation of the library version, or null if it has
	 * not been set in the meta-data
	 */
	public String getVersion() {
		return meta.getString(MetaData.VERSION);
	}
	
	/**
	 * Returns the date and time that the story graph was {@link
	 * MetaData#CREATED created}.
	 * 
	 * @return the timestamp the graph was created, or null if it has not been
	 * set in the graph's meta-data
	 */
	public Instant getCreated() {
		return meta.getInstant(MetaData.CREATED);
	}
	
	/**
	 * Returns the date and time that the story graph was {@link
	 * MetaData#MODIFIED last modified}.
	 * 
	 * @return the timestamp the graph was last modified, or null if it has not
	 * been set in the graph's meta-data
	 */
	public Instant getModified() {
		return meta.getInstant(MetaData.MODIFIED);
	}
	
	/**
	 * Returns the {@link MetaData#TITLE title} of the story graph.
	 * 
	 * @return the title, or null if it has not been set in the graph's
	 * meta-data
	 */
	public String getTitle() {
		return meta.getString(MetaData.TITLE);
	}
	
	/**
	 * Returns a label for the graph, which will be {@code story graph} if the
	 * graph has no {@link #getTitle() title} or {@code story graph "TITLE"} if
	 * the graph does have a title.
	 * 
	 * @return a label for the story graph
	 */
	private String getLabel() {
		String string = "story graph";
		String title = getTitle();
		if(title != null)
			string += " \"" + title + "\"";
		return string;
	}
	
	/**
	 * Sets the {@link MetaData#TITLE title} of the story graph.
	 * 
	 * @param name the new title for the story graph
	 */
	public void setTitle(String name) {
		setMetaData(MetaData.TITLE, name);
	}
	
	/**
	 * Returns the names of the {@link MetaData#AUTHORS authors} who created the
	 * the story graph.
	 * 
	 * @return the authors, or null if it has not been set in the graph's
	 * meta-data
	 */
	public String getAuthors() {
		return meta.getString(MetaData.AUTHORS);
	}
	
	/**
	 * Sets the names of the {@link MetaData#AUTHORS authors} who created the
	 * story graph.
	 * 
	 * @param string the new authors of the story graph
	 */
	public void setAuthors(String string) {
		setMetaData(MetaData.AUTHORS, string);
	}
	
	/**
	 * {@link MetaData#set(String, String) Sets} a {@link #meta meta-data} key
	 * to a given value and also updates the {@link MetaData#MODIFIED last
	 * modified} timestamp.
	 * 
	 * @param key the meta-data key to set
	 * @param value the value to be associated with the meta-data key
	 */
	protected void setMetaData(String key, String value) {
		meta.set(key, value);
		meta.set(MetaData.MODIFIED, Instant.now());
	}
	
	/**
	 * Removes all elements of a story graph for which the given predicate
	 * returns true and updates any elements which are affected by the removal
	 * of those elements. Note that elements may be indirectly removed as a
	 * result of pruning. For example, if a {@link Character character} is
	 * removed, any {@link EpistemicEdge epistemic edges} with that character
	 * as a label will also be removed. After pruning, the {@link Symbol#getID()
	 * ID numbers} of elements may be reassigned to ensure that every element's
	 * ID number is unique and sequential. For example, removing the character
	 * with ID number 1 will cause all characters after it to be renumbered, and
	 * a new character will now have the ID number 1.
	 * 
	 * @param predicate a predicate that returns true for all elements of the
	 * story graph that should be removed
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 */
	public void prune(Predicate<Object> predicate, Status status) {
		status.setMessage("Pruning " + getLabel());
		StoryGraphCollection<?>[] collections = new StoryGraphCollection[] {
			characters,
			fluents,
			values,
			states,
			actions,
			plans,
			nodes,
			edges,
			explanations
		};
		boolean modified = false;
		boolean[] renumber = new boolean[collections.length];
		for(int i = 0; i < collections.length; i++) {
			if(collections[i].prune(predicate, status)) {
				modified = true;
				renumber[i] = true;
			}
		}
		for(int i = 0; i < collections.length; i++)
			if(renumber[i])
				collections[i].renumber(status);
		if(modified)
			meta.set(MetaData.MODIFIED, Instant.now());
		status.setMessage("Pruned " + getLabel());
	}
	
	/**
	 * {@link #prune(Predicate, Status) Prunes} a story graph without reporting
	 * the method's progress while it runs.
	 * 
	 * @param predicate a predicate that returns true for all elements of the
	 * story graph that should be removed
	 * @see #prune(Predicate, Status)
	 */
	public void prune(Predicate<Object> predicate) {
		prune(predicate, new Status());
	}
	
	/**
	 * Reads a story graph from a {@link GraphReader story graph reader},
	 * creating all of the elements, nodes, and edges defined in the reader.
	 * 
	 * @param reader the source from which the story graph will be read
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @return a new story graph which has all elements defined by the reader
	 * @throws IOException if an exception occurs while reading from the reader
	 * or if elements of the graph are not formatted correctly in the reader
	 */
	public static StoryGraph from(GraphReader reader, Status status) throws IOException {
		StoryGraph graph = new StoryGraph(new MetaData(reader, status));
		graph.read(reader, status);
		return graph;
	}
	
	/**
	 * {@link #read(GraphReader, Status) Reads} a story graph from a {@link
	 * GraphReader story graph reader} without reporting the method's progress
	 * while it runs.
	 * 
	 * @param reader the source from which the story graph will be read
	 * @return a new story graph which has all elements defined by the reader
	 * @throws IOException if an exception occurs while reading from the reader
	 * or if elements of the graph are not formatted correctly in the reader
	 * @see #from(GraphReader, Status)
	 */
	public static StoryGraph from(GraphReader reader) throws IOException {
		return from(reader, new Status());
	}
	
	/**
	 * {@link #read(GraphReader, Status) Reads} a story graph from a {@link File
	 * file}. The file can be either a {@link DirectoryReader directory} or an
	 * {@link ArchiveReader archive file} which contains the necessary files.
	 * 
	 * @param file a directory or archive file that contains the files that
	 * define a story graph's elements
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @return a new story graph which has all elements defined by the reader
	 * @throws IOException if an exception occurs while reading from the file(s)
	 * or if elements of the graph are not formatted correctly in the file(s)
	 * @see #from(GraphReader, Status)
	 */
	public static StoryGraph from(File file, Status status) throws IOException {
		status.setMessage("Reading story graph from \"" + file + "\"");
		StoryGraph graph;
		if(file.isDirectory()) {
			try(DirectoryReader reader = new DirectoryReader(file)) {
				graph = from(reader, status);
			}
		}
		else {
			try(ArchiveReader reader = new ArchiveReader(file)) {
				graph = from(reader, status);
			}
		}
		status.setMessage("Read " + graph.getLabel() + " from \"" + file + "\"");
		return graph;
	}
	
	/**
	 * {@link #read(File, Status) Reads} a story graph from a file without
	 * reporting the method's progress while it runs.
	 * 
	 * @param file a directory or archive file that contains the files that
	 * define a story graph's elements
	 * @return a new story graph which has all elements defined by the reader
	 * @throws IOException if an exception occurs while reading from the file(s)
	 * or if elements of the graph are not formatted correctly in the file(s)
	 * @see #from(File, Status)
	 */
	public static StoryGraph from(File file) throws IOException {
		return from(file, new Status());
	}
	
	/**
	 * Removes all elements from this story graph, if any, and replaces them by
	 * reading elements from a {@link GraphReader story graph reader}.
	 * 
	 * @param reader the source from which the story graph will be read
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading from the reader
	 * or if elements of the graph are not formatted correctly in the reader
	 */
	public void read(GraphReader reader, Status status) throws IOException {
		status.setMessage("Reading " + getLabel());
		try {
			meta.read(reader, status);
			Instant modified = meta.getInstant(MetaData.MODIFIED);
			characters.read(reader, status);
			fluents.read(reader, status);
			values.read(reader, status);
			states.read(reader, status);
			actions.read(reader, status);
			plans.read(reader, status);
			nodes.read(reader, status);
			edges.read(reader, status);
			explanations.read(reader, status);
			if(modified != null)
				meta.set(MetaData.MODIFIED, modified);
		}
		catch(Exception exception) {
			if(exception instanceof IOException)
				throw (IOException) exception;
			else if(reader.getFileName() != null)
				throw Exceptions.ioException(exception, reader.getFileName(), reader.getLineNumber());
			else
				throw new IOException(exception);
		}
		status.setMessage("Read " + getLabel());
	}
	
	/**
	 * {@link #read(GraphReader, Status) Reads} elements from a {@link
	 * GraphReader story graph reader} without reporting the method's progress
	 * while it runs.
	 * 
	 * @param reader the source from which the story graph will be read
	 * @throws IOException if an exception occurs while reading from the reader
	 * or if elements of the graph are not formatted correctly in the reader
	 * @see #read(GraphReader, Status)
	 */
	public void read(GraphReader reader) throws IOException {
		read(reader, new Status());
	}
	
	/**
	 * Removes all elements from this story graph, if any, and replaces them by
	 * {@link #read(GraphReader, Status) reading} elements from a {@link File
	 * file}, which can be either a {@link DirectoryReader directory} or an
	 * {@link ArchiveReader archive file} which contains the necessary files.
	 * 
	 * @param file a directory or archive file that contains the files that
	 * define a story graph's elements
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading from the file(s)
	 * or if elements of the graph are not formatted correctly in the file(s)
	 * @see #read(GraphReader, Status)
	 */
	public void read(File file, Status status) throws IOException {
		status.setMessage("Reading " + getLabel() + " from \"" + file + "\"");
		if(file.isDirectory()) {
			try(DirectoryReader reader = new DirectoryReader(file)) {
				read(reader, status);
			}
		}
		else {
			try(ArchiveReader reader = new ArchiveReader(file)) {
				read(reader, status);
			}
		}
		status.setMessage("Read " + getLabel() + " from \"" + file + "\"");
	}
	
	/**
	 * {@link #read(File, Status) Reads} story graph elements from a {@link File
	 * file} without reporting the method's progress while it runs.
	 * 
	 * @param file a directory or archive file that contains the files that
	 * define a story graph's elements
	 * @throws IOException if an exception occurs while reading from the file(s)
	 * or if elements of the graph are not formatted correctly in the file(s)
	 * @see #read(GraphReader, Status)
	 */
	public void read(File file) throws IOException {
		read(file, new Status());
	}
	
	/**
	 * Writes all elements of this graph to a {@link GraphWriter story graph
	 * writer}. Before this method runs, the {@link #getVersion() version number}
	 * for this story graph will be set to this {@link Settings#VERSION_STRING
	 * library version number}.
	 * 
	 * @param writer the destination to which the story graph will be written
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing to the writer
	 */
	public void write(GraphWriter writer, Status status) throws IOException {
		status.setMessage("Writing " + getLabel());
		meta.set(MetaData.VERSION, Settings.VERSION_STRING);
		try {
			meta.write(writer, status);
			characters.write(writer, status);
			fluents.write(writer, status);
			values.write(writer, status);
			states.write(writer, status);
			actions.write(writer, status);
			plans.write(writer, status);
			nodes.write(writer, status);
			edges.write(writer, status);
			explanations.write(writer, status);
		}
		catch(Exception exception) {
			if(exception instanceof IOException)
				throw (IOException) exception;
			else if(writer.getFileName() != null)
				throw Exceptions.ioException(exception, writer.getFileName(), writer.getLineNumber());
			else
				throw new IOException(exception.getMessage(), exception);
		}
		status.setMessage("Wrote " + getLabel());
	}
	
	/**
	 * {@link #write(GraphWriter, Status) Writes} all elements of this graph to
	 * a {@link GraphWriter story graph writer} without reporting the method's
	 * progress while it runs.
	 * 
	 * @param writer the destination to which the story graph will be written
	 * @throws IOException if an exception occurs while writing to the writer
	 * @see #write(GraphWriter, Status)
	 */
	public void write(GraphWriter writer) throws IOException {
		write(writer, new Status());
	}
	
	/**
	 * {@link #write(GraphWriter, Status) Writes} all elements of this graph to
	 * a {@link File file}, which can be either a {@link DirectoryWriter
	 * directory} or an {@link ArchiveWriter archive file}.
	 * 
	 * @param file a directory or archive file to which the files that define
	 * this story graph's elements will be written
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing to the file(s)
	 * @see #write(GraphWriter, Status)
	 */
	public void write(File file, Status status) throws IOException {
		status.setMessage("Writing " + getLabel() + " to \"" + file + "\"");
		if(file.isDirectory()) {
			try(GraphWriter writer = new DirectoryWriter(file)) {
				write(writer, status);
			}
		}
		else {
			try(GraphWriter writer = new ArchiveWriter(file)) {
				write(writer, status);
			}
		}
		status.setMessage("Wrote " + getLabel() + " to \"" + file + "\"");
	}
	
	/**
	 * {@link #write(GraphWriter, Status) Writes} all elements of this graph to
	 * a {@link File file} without reporting the method's progress while it
	 * runs.
	 * 
	 * @param file a directory or archive file to which the files that define
	 * this story graph's elements will be written
	 * @throws IOException if an exception occurs while writing to the file(s)
	 * @see #write(GraphWriter, Status)
	 */
	public void write(File file) throws IOException {
		write(file, new Status());
	}
}