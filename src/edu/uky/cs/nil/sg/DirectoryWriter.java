package edu.uky.cs.nil.sg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A {@link GraphWriter story graph writer} that writes to a directory. Each of
 * the stroy graph's {@link #getFileName() files} will be files in the
 * directory.
 * 
 * @author Stephen G. Ware
 */
public class DirectoryWriter implements GraphWriter {
	
	/** The directory in which all graph files are stored */
	private final File directory;
	
	/** The currently open file */
	private String file = null;
	
	/** The writer for the currently open file */
	private BufferedWriter writer = null;
	
	/** The last line written to the currently open entry */
	private long line;
	
	/**
	 * Constructs a new directory graph writer for a given directory, creating
	 * the directory if it does not already exist.
	 * 
	 * @param file the directory that will store the story graph's files
	 * @throws IOException if the given directory does not exist and cannot be
	 * created
	 * @throws java.lang.SecurityException if the directory cannot be read
	 * due to the current security manager
	 */
	public DirectoryWriter(File file) throws IOException {
		if(!file.exists())
			file.mkdir();
		this.directory = file;
	}
	
	@Override
	public String getFileName() {
		return file;
	}
	
	@Override
	public long getLineNumber() {
		if(getFileName() == null)
			throw Exceptions.fileNotOpen();
		else
			return line;
	}
	
	@Override
	public void setFile(String name) throws IOException {
		try {
			if(writer != null)
				writer.close();
		}
		finally {
			file = null;
			writer = null;
			line = 0;
		}
		File child = new File(directory, name);
		writer = new BufferedWriter(new FileWriter(child));
	}
	
	@Override
	public void writeNextLine(String line) throws IOException {
		writer.append(line);
		writer.append("\n");
	}
	
	@Override
	public void close() throws IOException {
		try {
			if(writer != null)
				writer.close();
		}
		finally {
			file = null;
			writer = null;
		}
	}
}