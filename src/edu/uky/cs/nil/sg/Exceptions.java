package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.NoSuchElementException;

/**
 * Static methods for creating story graph exceptions.
 * 
 * @author Stephen G. Ware
 */
class Exceptions {
	
	/**
	 * Creates a {@link NullPointerException} when some type of element cannot
	 * be null.
	 * 
	 * @param type the type of object that cannot be null
	 * @return a {@link NullPointerException}
	 */
	public static NullPointerException cannotBeNull(String type) {
		return new NullPointerException(Utilities.capitalize(type) + " cannot be null.");
	}
	
	/**
	 * Creates a {@link NoSuchElementException} for when an {@link
	 * java.util.Iterator} has no more elements.
	 * 
	 * @return a {@link NoSuchElementException}
	 */
	public static NoSuchElementException iteratorEmpty() {
		return new NoSuchElementException("The iterator has no more elements.");
	}
	
	/**
	 * Creates an {@link IndexOutOfBoundsException} when attempting to access an
	 * index that does not exist.
	 * 
	 * @param index the index that does not exist
	 * @param size the size of the collection in which the index is being
	 * accessed
	 * @return an {@link IndexOutOfBoundsException}
	 */
	public static IndexOutOfBoundsException indexOutOfBounds(long index, long size) {
		if(index < 0)
			return new IndexOutOfBoundsException("The index " + index + " does not exist; an index must be positive.");
		else
			return new IndexOutOfBoundsException("The index " + index + " does not exist in a collection of size " + size + ".");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} for when a {@link
	 * BigArrayList} would need to expand past {@link
	 * BigArrayList#MAX_CAPACITY}.
	 * 
	 * @return an {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException listCapacityTooHigh() {
		return new IllegalArgumentException("The list cannot exceed its maximum capacity of " + BigArrayList.MAX_CAPACITY + ".");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} for when a value cannot be
	 * converted into a value of a certain type.
	 * 
	 * @param value the object that cannot be converted
	 * @param type the type of thing the object cannot be converted to
	 * @return an {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException cannotConvert(Object value, String type) {
		return new IllegalArgumentException("The value \"" + value + "\" cannot be converted to " + type + ".");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} for when a {@link MetaData
	 * meta-data key} does not have a value defined.
	 * 
	 * @param key the meta-data key that has no value defined
	 * @return an {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException metaDataKeyNotDefined(String key) {
		return new IllegalArgumentException("The meta-data key \"" + key + "\" is not defined.");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} for when an object with a
	 * certain ID number does not exist.
	 * 
	 * @param type the type of object that does not exist
	 * @param id the ID number of the object that does not exist
	 * @return an {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException idNotDefined(String type, long id) {
		return new IllegalArgumentException("The " + type + " with ID number " + id + " is not defined.");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} for when an object with a
	 * certain name does not exist.
	 * 
	 * @param type the type of object that does not exist 
	 * @param name the name of the object that does not exist
	 * @return an {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException nameNotDefined(String type, String name) {
		return new IllegalArgumentException("The " + type + " \"" + name + "\" is not defined.");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} for when an object is not
	 * part of a {@link StoryGraph story graph}, either because it has been
	 * removed or because it was created by a different story graph.
	 * 
	 * @param type the type of object that does not exist in the correct graph
	 * @param element the object that does not exist in the correct graph
	 * @return an {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException invalid(String type, Object element) {
		return new IllegalArgumentException("The " + type + " \"" + element + "\" either does not exist or was created by a different story graph.");
	}
	
	/**
	 * Creates an {@link IllegalArgumentException} for when a new edge being
	 * created would be a duplicate edge, which is not allowed.
	 * 
	 * @param type the type of edge
	 * @param tail the tail node of the duplicate edge
	 * @param label the label of the duplicate edge
	 * @return an {@link IllegalArgumentException}
	 */
	public static IllegalArgumentException duplicateEdge(String type, Node tail, Object label) {
		return new IllegalArgumentException("The " + type + " from node " + tail.getID() + " for \"" + label + "\" already exists.");
	}
	
	/**
	 * Creates an {@link IOException} that includes information on the {@link
	 * GraphReader#getFileName() file name} and {@link
	 * GraphReader#getLineNumber() line number} where the problem occurred.
	 * 
	 * @param cause an exception that occurred while reading a story graph
	 * @param file the name of the file where the exception occurred
	 * @param line the line number of the file where the exception occurred
	 * @return an {@link IOException}
	 */
	public static IOException ioException(Exception cause, String file, long line) {
		return new IOException(cause.getMessage() + " (in file \"" + file + "\" at line " + line + ")", cause);
	}
	
	/**
	 * Creates a {@link NoSuchFileException} for when a {@link GraphReader story
	 * graph reader} is opened for a file that does not exist.
	 * 
	 * @param url the name of the file that does not exist
	 * @return a {@link NoSuchFileException}
	 */
	public static NoSuchFileException fileNotFound(String url) {
		return new NoSuchFileException("The file \"" + url + "\" cound not be found.");
	}
	
	/**
	 * Creates an {@link IllegalStateException} for when a {@link GraphReader
	 * story graph reader} or {@link GraphWriter writer} tried to read or write
	 * when no file is open.
	 * 
	 * @return an {@link IllegalStateException}
	 */
	public static IllegalStateException fileNotOpen() {
		return new IllegalStateException("Cannot read or write the next line, because no file is currently open.");
	}
	
	/**
	 * Creates an {@link IOException} for when a {@link GraphReader story graph
	 * reader} expects a line {@link GraphReader#readNextLineAsCSV(int) in a CSV
	 * file} to have a certain number of elements but it has a different number.
	 * 
	 * @param expected the number of elements the CSV line is expected to have
	 * @param given the number of elements the CSV line actually has
	 * @param file the file from which the line was read
	 * @param line the line number of the line that was read
	 * @return an {@link IOException}
	 */
	public static IOException wrongNumberOfColumns(int expected, int given, String file, long line) {
		return new IOException("Expected " + expected + " columns but found " + given + " in CSV file \"" + file + "\", line " + line + ".");
	}
	
	/**
	 * Creates an {@link IOException} for when a {@link DirectoryReader story
	 * graph directory reader} is created for a URL that is not a directory.
	 * 
	 * @param url the URL which is not a directory
	 * @return an {@link IOException}
	 */
	public static IOException notDirectory(String url) {
		return new IOException("The URL \"" + url + "\" is not a directory.");
	}
}