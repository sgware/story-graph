package edu.uky.cs.nil.sg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A {@link GraphReader story graph reader} that reads from a directory. Each of
 * the story graph's {@link #getFileName() files} are files in the directory.
 * 
 * @author Stephen G. Ware
 */
public class DirectoryReader implements GraphReader {
	
	/** The directory in which all graph files are stored */
	private final File directory;
	
	/** The currently open file */
	private String file = null;
	
	/** The reader for the currently open entry */
	private BufferedReader reader = null;
	
	/** The last line read from the currently open entry */
	private long line;
	
	/**
	 * Constructs a new directory graph reader from a given directory.
	 * 
	 * @param file the directory that stores a story graph's files
	 * @throws IOException if the given file is not a directory
	 * @throws java.lang.SecurityException if the directory cannot be read
	 * due to the current security manager
	 */
	public DirectoryReader(File file) throws IOException {
		if(!file.isDirectory())
			throw Exceptions.notDirectory(file.getName());
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
	public boolean setFile(String name) throws IOException {
		try {
			if(reader != null)
				reader.close();
		}
		finally {
			file = null;
			reader = null;
			line = 0;
		}
		File child = new File(directory, name);
		if(child.exists()) {
			file = name;
			reader = new BufferedReader(new FileReader(child));
			return true;
		}
		else
			return false;
	}

	@Override
	public String readNextLine() throws IOException {
		return reader.readLine();
	}

	@Override
	public void close() throws IOException {
		file = null;
		try {
			if(reader != null)
				reader.close();
		}
		finally {
			file = null;
			reader = null;
		}
	}
}