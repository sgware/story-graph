package edu.uky.cs.nil.sg;

import java.io.IOException;

/**
 * An interface for reading a {@link StoryGraph story graph} from an input
 * source. Though a story graph is segmented into "files," the source does not
 * actually need to store the data in files as long as segments can be accessed
 * via the {@link #setFile(String)} method.
 * 
 * @author Stephen G. Ware
 */
public interface GraphReader extends AutoCloseable {
	
	/**
	 * Returns the name of the current file being read from, or null if no file
	 * is currently being read.
	 * 
	 * @return the name of the current file, or null is there is no current file
	 */
	public String getFileName();
	
	/**
	 * Returns the line number most recently read from the {@link #getFileName()
	 * current file}. A line number of 0 means no lines have yet been read.
	 * 
	 * @return the line number most recently read
	 * @throws IllegalStateException if no file is currently open
	 */
	public long getLineNumber();
	
	/**
	 * Opens the given file for reading, closing the currently open file if one
	 * is open.
	 * 
	 * @param name the name of the file to open
	 * @return true if the file exists, false if it does not exist
	 * @throws IOException if an exception occurs while opening the file
	 */
	public boolean setFile(String name) throws IOException;
	
	/**
	 * Reads and returns the next line from the {@link #getFileName() currently
	 * open file}.
	 * 
	 * @return the next line from the file, or null if there are no more lines
	 * @throws IOException if an error occurs while reading from the file
	 */
	public String readNextLine() throws IOException;
	
	/**
	 * {@link #readNextLine() Reads the next line} from the {@link
	 * #getFileName() currently open file}, {@link Utilities#unquote(String)
	 * removes quotes}, {@link Utilities#unquote(String) replaces escaped
	 * special characters}, and returns the resulting string.
	 * 
	 * @return the next line from the file with quotes removed and escaped
	 * special characters replaced, or null if there are no more lines
	 * @throws IOException if an error occurs while reading from the file
	 * @see #readNextLine()
	 * @see Utilities#unquote(String)
	 */
	public default String readNextLineAsString() throws IOException {
		String line = readNextLine();
		if(line == null)
			return null;
		else
			return Utilities.unquote(line);
	}
	
	/**
	 * {@link #readNextLine() Reads the next line} from the {@link
	 * #getFileName() currently open file}, separates the line where commas
	 * appear, {@link Utilities#unquote(String) unquotes} each string, and
	 * returns them in an array.
	 * 
	 * @return an array of the strings that were separated by commas, with the
	 * commas and quotes removed and with escaped special characters replaced,
	 * or null if there are no more lines
	 * @throws IOException if an error occurs while reading from the file
	 * @see #readNextLine()
	 * @see Utilities#unquote(String)
	 */
	public default String[] readNextLineAsCSV() throws IOException {
		String line = readNextLine();
		if(line == null)
			return null;
		else if(line.isEmpty())
			return new String[0];
		else {
			String[] array = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
			for(int i=0; i<array.length; i++) {
				if(array[i].equals(""))
					array[i] = null;
				else
					array[i] = Utilities.unquote(array[i]);
			}
			return array;
		}
	}
	
	/**
	 * {@link #readNextLine() Reads the next line} from the {@link
	 * #getFileName() currently open file}, separates the line where commas
	 * appear, {@link Utilities#unquote(String) unquotes} each string, and
	 * returns them in an array of the given length, or throws an exception
	 * if the number of comma-separated strings does not match the expected
	 * number.
	 * 
	 * @param columns the number of comma-separated string the line is expected
	 * to have
	 * @return an array of the strings that were separated by commas, with the
	 * commas and quotes removed and with escaped special characters replaced,
	 * or null if there are no more lines
	 * @throws IOException if an error occurs while reading from the file or if
	 * the number of comma-separated values does not match the expected number
	 * @see #readNextLine()
	 * @see Utilities#unquote(String)
	 */
	public default String[] readNextLineAsCSV(int columns) throws IOException {
		String[] line = readNextLineAsCSV();
		if(line == null || line.length == columns)
			return line;
		else
			throw Exceptions.wrongNumberOfColumns(columns, line.length, getFileName(), getLineNumber());
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method first closes the {@link #getFileName() currently open file}
	 * and then closes the graph reader.
	 */
	@Override
	public void close() throws IOException;
}