package edu.uky.cs.nil.sg;

import java.io.IOException;

/**
 * An interface for writing a {@link StoryGraph story graph} to an output
 * destination. Though a story graph is segmented into "files," the destination
 * does not actually need to store the data in files as long as segments can be
 * written to via the {@link #setFile(String)} method.
 * 
 * @author Stephen G. Ware
 */
public interface GraphWriter extends AutoCloseable {
	
	/**
	 * Returns the name of the current file being written to, or null if no file
	 * is currently being written.
	 * 
	 * @return the name of the current file, or null is there is no current file
	 */
	public String getFileName();
	
	/**
	 * Returns the line number most recently written to the {@link
	 * #getFileName() current file}. A line number of 0 means no lines have yet
	 * been written.
	 * 
	 * @return the line number most recently written
	 * @throws IllegalStateException if no file is currently open
	 */
	public long getLineNumber();
	
	/**
	 * Opens the given file for writing, closing the currently open file if one
	 * is open.
	 * 
	 * @param name the name of the file to open
	 * @throws IOException if an exception occurs while opening the file
	 */
	public void setFile(String name) throws IOException;
	
	/**
	 * Writes a given string to the next line of the {@link #getFileName()
	 * currently open file}. A new line character is automatically appended
	 * after the string is written.
	 * 
	 * @param line the string to write to the file
	 * @throws IOException if an error occurs while writing to the file
	 */
	public void writeNextLine(String line) throws IOException;
	
	/**
	 * {@link #writeNextLine(String) Writes a line} to the {@link
	 * #getFileName() currently open file}, {@link Utilities#quote(String)
	 * wrapping it in quotes and escaping special characters} if it contains any
	 * double quote, new line, or carriage return characters.
	 * 
	 * @param string the string to write to file
	 * @throws IOException if an error occurs while writing to the file
	 * @see #writeNextLine(String)
	 * @see Utilities#quote(String)
	 */
	public default void writeNextLineAsString(String string) throws IOException {
		if(string.contains("\n") || string.contains("\r") || string.contains("\""))
			string = Utilities.quote(string);
		writeNextLine(string);
	}
	
	/**
	 * {@link #writeNextLine(String) Writes} a series of values as
	 * comma-separated strings to the {@link #getFileName() currently open
	 * file}, {@link Utilities#quote(String) wrapping each in quotes and
	 * escaping special characters} if they contain any double quote, new line,
	 * or carriage return characters.
	 * 
	 * @param array the objects to write to file
	 * @throws IOException if an error occurs while writing to the file
	 * @see #writeNextLine(String)
	 * @see Utilities#quote(String)
	 */
	public default void writeNextLineAsCSV(Object...array) throws IOException {
		String line = "";
		for(int i=0; i<array.length; i++) {
			if(i > 0)
				line += ",";
			if(array[i] == null)
				continue;
			else if(array[i] instanceof Symbol)
				line += ((Symbol) array[i]).getID();
			else if(array[i] instanceof NumericValue || array[i] instanceof Double) {
				String s = array[i].toString();
				if(!s.contains(".") && !(s.equals(Double.toString(Double.NaN)) || s.equals(Double.toString(Double.POSITIVE_INFINITY)) || s.equals(Double.toString(Double.NEGATIVE_INFINITY))))
					s += ".0";
				line += s;
			}
			else if(array[i] instanceof Numbered)
				line += ((Numbered) array[i]).getID();
			else {
				String s = array[i].toString();
				if(s.contains("\n") || s.contains("\r") || s.contains(",") || s.contains("\""))
					s = Utilities.quote(s);
				line += s;
			}
		}
		writeNextLine(line);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method first closes the {@link #getFileName() currently open file}
	 * and then closes the graph writer.
	 */
	@Override
	public void close() throws IOException;
}