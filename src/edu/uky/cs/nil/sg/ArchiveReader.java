package edu.uky.cs.nil.sg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A {@link GraphReader story graph reader} that reads from a ZIP archive file.
 * Each of the stroy graph's {@link #getFileName() "files"} are {@link ZipEntry
 * entries} in the archive file.
 * 
 * @author Stephen G. Ware
 */
public class ArchiveReader implements GraphReader {
	
	/** The archive file in which all graph files are stored as entries */
	private final ZipFile file;
	
	/** The currently open entry */
	private ZipEntry entry = null;
	
	/** The reader for the currently open entry */
	private BufferedReader reader = null;
	
	/** The last line read from the currently open entry */
	private long line;
	
	/**
	 * Constructs a new archive graph reader from a given archive file.
	 * 
	 * @param file the archive file that stores a story graph
	 * @throws IOException if an error occurs while opening the file
	 * @throws java.nio.file.NoSuchFileException if the file does not exist
	 * @throws java.util.zip.ZipException if the archive file is not formatted
	 * correctly
	 * @throws java.lang.SecurityException if the archive file cannot be opened
	 * due to the current security manager
	 */
	public ArchiveReader(File file) throws IOException {
		try {
			this.file = new ZipFile(file);
		}
		catch(FileNotFoundException | NoSuchFileException exception) {
			throw Exceptions.fileNotFound(file.getName());
		}
	}
	
	@Override
	public String getFileName() {
		if(entry == null)
			return null;
		else
			return entry.getName();
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
			entry = null;
			reader = null;
			line = 0;
		}
		entry = file.getEntry(name);
		if(entry == null)
			return false;
		else {
			reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)));
			return true;
		}
	}
	
	@Override
	public String readNextLine() throws IOException {
		if(entry == null)
			throw Exceptions.fileNotOpen();
		String line = reader.readLine();
		if(line != null)
			this.line++;
		return line;
	}
	
	@Override
	public void close() throws IOException {
		entry = null;
		IOException exception = null;
		try {
			if(reader != null)
				reader.close();
		}
		catch(IOException e) {
			exception = e;
		}
		finally {
			reader = null;
		}
		try {
			file.close();
		}
		catch(IOException e) {
			if(exception == null)
				exception = e;
		}
		if(exception != null)
			throw exception;
	}
}