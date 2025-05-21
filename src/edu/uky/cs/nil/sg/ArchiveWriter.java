package edu.uky.cs.nil.sg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A {@link GraphWriter story graph writer} that writes to a ZIP archive file.
 * Each of the stroy graph's {@link #getFileName() "files"} will be {@link
 * ZipEntry entries} in the archive file.
 * 
 * @author Stephen G. Ware
 */
public class ArchiveWriter implements GraphWriter {
	
	/** The archive file in which all graph files are stored as entries */
	private final ZipOutputStream file;
	
	/** The currently open entry */
	private ZipEntry entry = null;
	
	/** The last line written to the currently open entry */
	private long line;
	
	/**
	 * Constructs a new archive graph writer for a given archive file.
	 * 
	 * @param file the archive file that will store a story graph
	 * @throws IOException if an error occurs while opening the file
	 * @throws java.lang.SecurityException if the archive file cannot be opened
	 * due to the current security manager
	 */
	public ArchiveWriter(File file) throws IOException {
		this.file = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
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
	public void setFile(String name) throws IOException {
		try {
			if(file != null)
				file.closeEntry();
		}
		finally {
			entry = null;
			line = 0;
		}
		entry = new ZipEntry(name);
		file.putNextEntry(entry);
	}

	@Override
	public void writeNextLine(String line) throws IOException {
		if(entry == null)
			throw Exceptions.fileNotOpen();
		file.write(line.getBytes());
		file.write("\n".getBytes());
		this.line++;
	}
	
	@Override
	public void close() throws IOException {
		entry = null;
		file.close();
	}
}