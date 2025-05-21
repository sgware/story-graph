package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A table for storing meta information about a {@link StoryGraph story graph}.
 * Every piece of meta-data has a string key. Values can be of several types,
 * including strings, numbers, and {@link Instant timestamps}.
 * <p>
 * One common piece of meta-data stored in this table is the number of elements
 * of various kinds of the story graph (for example, the {@link #CHARACTERS
 * number of character symbols}). By storing this information, a graph can
 * allocate collections of exactly the right size before reading a potentially
 * large number of elements from file.
 * 
 * @author Stephen G. Ware
 */
public class MetaData {
	
	/**
	 * Meta-data key for the version number of the story graph library used to
	 * create this story graph
	 */
	public static final String VERSION = "version";
	
	/** Meta-data key for the timestamp when this graph was created */
	public static final String CREATED = "created";
	
	/** Meta-data key for the timestemp when this graph was last modified */
	public static final String MODIFIED = "modified";
	
	/** Meta-data key for the title of the story graph */
	public static final String TITLE = "title";
	
	/** Meta-data key for the names of the creators of the story graph */
	public static final String AUTHORS = "authors";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#characters characters}
	 * in the story graph
	 */
	public static final String CHARACTERS = "characters";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#fluents fluents} in the
	 * story graph
	 */
	public static final String FLUENTS = "fluents";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#values nominal values}
	 * in the story graph
	 */
	public static final String VALUES = "values";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#actions actions} in the
	 * story graph
	 */
	public static final String ACTIONS = "actions";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#states states} in the
	 * story graph
	 */
	public static final String STATES = "states";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#nodes nodes} in the
	 * story graph
	 */
	public static final String NODES = "nodes";
	
	/**
	 * Meta-data key for the number of {@link EdgeCollection#temporal temporal
	 * edges} in the story graph
	 */
	public static final String TEMPORAL = "temporal";
	
	/**
	 * Meta-data key for the number of {@link EdgeCollection#epistemic epistemic
	 * edges} in the story graph
	 */
	public static final String EPISTEMIC = "epistemic";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#plans plans} in the
	 * story graph
	 */
	public static final String PLANS = "plans";
	
	/**
	 * Meta-data key for the number of {@link StoryGraph#explanations
	 * explanations} in the story graph
	 */
	public static final String EXPLANATIONS = "explanations";
	
	/** Maps keys to values (as strings) */
	private final LinkedHashMap<String, String> map = new LinkedHashMap<>();
	
	/**
	 * Reads a meta-data table from a {@link GraphReader story graph reader}.
	 * 
	 * @param reader a story graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an error occurs while reading the file
	 */
	protected MetaData(GraphReader reader, Status status) throws IOException {
		read(reader, status);
	}
	
	/**
	 * Constructs a new meta-data table. The {@link #VERSION} key will be set
	 * to {@link Settings#VERSION_STRING}, and the {@link #CREATED} and {@link
	 * #MODIFIED} keys will be set to {@link Instant#now()}.
	 */
	public MetaData() {
		set(VERSION, Settings.VERSION_STRING);
		Instant created = Instant.now();
		set(CREATED, created);
		set(MODIFIED, created);
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
	
	/**
	 * Returns the value associated with a key as a string, or null if the key
	 * has no value.
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as a string
	 * @see #requireString(String)
	 */
	public String getString(String key) {
		return map.get(key);
	}
	
	/**
	 * Returns the value associated with a key as a string, or throws an
	 * exception if the key has no value.
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as a string
	 * @throws IllegalArgumentException if the key has no value
	 * @see #getString(String)
	 */
	public String requireString(String key) {
		return require(key, getString(key));
	}
	
	/**
	 * Returns the value associated with a key as an integer, or null if the key
	 * has no value.
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as an integer
	 * @throws NumberFormatException if the key's value is not null but cannot
	 * be parsed as an integer
	 * @see #requireInteger(String)
	 */
	public Integer getInteger(String key) {
		String string = getString(key);
		if(string == null)
			return null;
		return Utilities.toInteger(string);
	}
	
	/**
	 * Returns the value associated with a key as an integer, or throws an
	 * exception if the key has no value.
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as an integer
	 * @throws IllegalArgumentException if the key has no value
	 * @throws NumberFormatException if the key's value is not null but cannot
	 * be parsed as an integer
	 * @see #getInteger(String)
	 */
	public int requireInteger(String key) {
		return require(key, getInteger(key));
	}
	
	/**
	 * Returns the value associated with a key as a long, or null if the key has
	 * no value.
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as a long
	 * @throws NumberFormatException if the key's value is not null but cannot
	 * be parsed as a long
	 * @see #requireLong(String)
	 */
	public Long getLong(String key) {
		String string = getString(key);
		if(string == null)
			return null;
		return Utilities.toLong(string);
	}
	
	/**
	 * Returns the value associated with a key as a long, or throws an exception
	 * if the key has no value.
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as a long
	 * @throws IllegalArgumentException if the key has no value
	 * @throws NumberFormatException if the key's value is not null but cannot
	 * be parsed as a long
	 * @see #getLong(String)
	 */
	public long requireLong(String key) {
		return require(key, getLong(key));
	}
	
	
	/**
	 * Returns the value associated with a key as a {@link Instant timestamp},
	 * or null if the key has no value. Timestamps are stored as the number of
	 * milliseconds since the Epoch (1970-01-01T00:00:00Z).
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as a timestamp
	 * @throws NumberFormatException if the key's value is not null but cannot
	 * be parsed as a long
	 * @see #requireInstant(String)
	 */
	public Instant getInstant(String key) {
		Long timestamp = getLong(key);
		if(timestamp == null)
			return null;
		return Instant.ofEpochMilli(timestamp);
	}
	
	/**
	 * Returns the value associated with a key as a {@link Instant timestamp},
	 * or throws an exception if the key has no value.
	 * 
	 * @param key the meta-data key whose value is desired
	 * @return the value associated with the key, as a timestamp
	 * @throws IllegalArgumentException if the key has no value
	 * @throws NumberFormatException if the key's value is not null but cannot
	 * be parsed as a long
	 * @see #getInstant(String)
	 */
	public Instant requireInstant(String key) {
		return require(key, getInstant(key));
	}
	
	/**
	 * Returns the given value or throws an exception if it is null.
	 * 
	 * @param <T> the type of value to be returned
	 * @param key the key, which will be used in the message of the exception if
	 * one is thrown
	 * @param value the value to be returned, or null
	 * @return the value
	 * @throws IllegalArgumentException if the value is null
	 */
	private static <T> T require(String key, T value) {
		if(value == null)
			throw Exceptions.metaDataKeyNotDefined(key);
		else
			return value;
	}
	
	/**
	 * Associates a string value with a key.
	 * 
	 * @param key the meta-data key whose value will be set
	 * @param string the value to associate with the key
	 */
	public void set(String key, String string) {
		map.put(key, string);
	}
	
	/**
	 * Associates an integer value with a key.
	 * 
	 * @param key the meta-data key whose value will be set
	 * @param number the value to associate with the key
	 */
	public void set(String key, int number) {
		set(key, Integer.toString(number));
	}
	
	/**
	 * Associates a long value with a key.
	 * 
	 * @param key the meta-data key whose value will be set
	 * @param number the value to associate with the key
	 */
	public void set(String key, long number) {
		set(key, Long.toString(number));
	}
	
	/**
	 * Associates a {@link Instant timestamp} value with a key.
	 * 
	 * @param key the meta-data key whose value will be set
	 * @param instant the value to associate with the key
	 */
	public void set(String key, Instant instant) {
		set(key, instant.toEpochMilli());
	}
	
	/** The name of the file in which the meta-data table is stored */
	private static final String METADATA_FILE = "meta.csv";
	
	/**
	 * {@link Map#clear() Clears} the table of all data and reads in key/value
	 * pairs from a {@link GraphReader story graph reader}.
	 * 
	 * @param reader a story graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an error occurs while reading the table
	 */
	protected void read(GraphReader reader, Status status) throws IOException {
		status.setMessage("Reading metadata");
		map.clear();
		if(reader.setFile(METADATA_FILE)) {
			String[] line = reader.readNextLineAsCSV(2);
			while(line != null) {
				set(line[0], line[1]);
				status.increment();
				line = reader.readNextLineAsCSV(2);
			}
		}
		status.setMessage("Read " + status.getCount() + " meta-data entries");
	}
	
	/**
	 * Writes all key/value pairs to file in a {@link GraphWriter story graph
	 * writer}.
	 * 
	 * @param writer a story graph writer
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an error occurs while writing the table
	 */
	protected void write(GraphWriter writer, Status status) throws IOException {
		status.set("Writing metadata", (long) map.size());
		writer.setFile(METADATA_FILE);
		for(Map.Entry<String, String> entry : map.entrySet()) {
			writer.writeNextLineAsCSV(entry.getKey(), entry.getValue());
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " meta-data entries");
	}
}