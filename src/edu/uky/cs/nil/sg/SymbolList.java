package edu.uky.cs.nil.sg;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@link StoryGraphList collection} of {@link Symbol symbols} of the same
 * type. A symbol can be accessed by its {@link #get(int) ID number} or {@link
 * #get(String) name}.
 * 
 * @param <S> the type of symbol in this collection
 * @author Stephen G. Ware
 */
public abstract class SymbolList<S extends Symbol> extends StoryGraphList<S> {
	
	/** A list of symbols by ID number */
	private final List<S> list;
	
	/** A map of symbol names to symbols */
	private final Map<String, S> map;
	
	/** An unmodifiable view of the {@link #list symbol list} */
	private final Iterable<S> iterable;
	
	/**
	 * Constructs a new symbol list. If the {@link StoryGraph#meta story graph's
	 * meta-data} specifies the number of elements in the list, it will be
	 * initialized with exactly that capacity.
	 * 
	 * @param graph the story graph to which the symbols belong
	 */
	protected SymbolList(StoryGraph graph) {
		super(graph);
		Integer size = graph.meta.getInteger(getMetaDataKey());
		if(size == null) {
			list = new ArrayList<>();
			map = new HashMap<>();
		}
		else {
			list = new ArrayList<>(size);
			map = new HashMap<>(size);
		}
		this.iterable = Collections.unmodifiableList(list);
	}
	
	@Override
	public String toString() {
		String string = "[" + Utilities.capitalize(getSingular()) + " List";
		String name = graph.meta.getString(MetaData.TITLE);
		if(name != null)
			string += " for \"" + name + "\"";
		string += ": " + size() + " " + getPlural() + "]";
		return string;
	}
	
	@Override
	public Iterator<S> iterator() {
		return iterable.iterator();
	}
	
	@Override
	public void forEach(Consumer<? super S> consumer) {
		list.forEach(consumer);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * By default, this method appends {@code ".txt"} to the result of {@link
	 * #getPlural()}.
	 */
	@Override
	protected String getFileName() {
		return getPlural() + ".txt";
	}
	
	/**
	 * Returns the number of symbols in the collection.
	 * 
	 * @return the number of symbols
	 */
	public int size() {
		return list.size();
	}
	
	/**
	 * Returns the symbol with the given {@link Symbol#getID() ID number}.
	 * 
	 * @param index the unique ID number of the desired symbol
	 * @return the symbol with that ID number
	 * @throws IndexOutOfBoundsException if the index is negative
	 * @throws IllegalArgumentException if the index is greater than or equal to
	 * the {@link #size() size} of the collection
	 */
	public S get(int index) {
		if(index < 0)
			throw Exceptions.indexOutOfBounds(index, size());
		else if(index >= size())
			throw Exceptions.idNotDefined(getSingular(), index);
		else
			return list.get(index);
	}
	
	/**
	 * Returns the symbol with the given {@link Symbol#name name}, or null if no
	 * symbol in the collection has that name.
	 * 
	 * @param name the name of the desired symbol
	 * @return the symbol with that name, or null if no such symbol is in the
	 * collection
	 * @see #require(String)
	 */
	public S get(String name) {
		return map.get(name);
	}
	
	/**
	 * Returns the symbol with the given {@link Symbol#name name}, or throws an
	 * exception if no symbol in the collection has that name.
	 * 
	 * @param name the name of the desired symbol
	 * @return the symbol with that name
	 * @throws IllegalArgumentException if no symbol in the collection has that
	 * name
	 * @see #get(String)
	 */
	public S require(String name) {
		S symbol = get(name);
		if(symbol == null)
			throw Exceptions.nameNotDefined(getSingular(), name);
		return symbol;
	}
	
	@Override
	protected boolean validate(Object object) {
		return object instanceof Symbol s && s.getID() >= 0 && s.getID() < size() && get(s.getID()) == s;
	}
	
	@Override
	protected void require(S symbol) {
		if(!validate(symbol))
			throw Exceptions.invalid(getSingular(), symbol.name);
	}
	
	/**
	 * Creates a new symbol of this type with the given name, or returns the
	 * existing symbol with that name if one already exists. If a new symbol is
	 * created, its {@link Symbol#getID() ID number} will be the next in the
	 * sequence (i.e. the {@link #size() size} of this collection at the time
	 * the symbol is created).
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * with the new number of symbols of {@link #getMetaDataKey() this type}.
	 * It also updates the {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param name the name of the symbol to create or find
	 * @return a new symbol with that given name, or the existing symbol with
	 * that name if one already exists
	 */
	public abstract S add(String name);
	
	/**
	 * Adds a new symbol to this collection. This method expects the {@link
	 * Symbol#getID() ID number} of the symbol to be the {@link #size() size} of
	 * the collection; in other words, adding the new symbol to the end of the
	 * {@link #list list of symbols} should add the symbol at the index that
	 * matches its ID number.
	 * <p>
	 * This method updates the {@link StoryGraph#meta story graph's meta-data}
	 * with the new number of symbols of {@link #getMetaDataKey() this type}. It
	 * also updates the {@link MetaData#MODIFIED last modified} timestamp.
	 * 
	 * @param symbol the new symbol to be added to the collection
	 */
	protected void add(S symbol) {
		list.add(symbol);
		map.put(symbol.name, symbol);
		graph.meta.set(getMetaDataKey(), size());
		graph.meta.set(MetaData.MODIFIED, Instant.now());
	}
	
	/**
	 * Sorts and renumbers the symbols of this collection based on the given
	 * comparator. If the order of any symbols changes, their {@link
	 * Symbol#getID() ID numbers} will be reassigned to be sequential.
	 * <p>
	 * If this method modifies the order of elements, it updates the {@link
	 * StoryGraph#meta story graph's meta-data} {@link MetaData#MODIFIED last
	 * modified} timestamp.
	 * 
	 * @param comparator the comparator that defines the new order of elements
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 */
	public void sort(Comparator<? super S> comparator, Status status) {
		status.set("Sorting " + getPlural(), (long) size());
		if(size() > 0)
			status.setCount(1);
		boolean modified = false;
		for(int start = 1; start < size(); start++) {
			for(int i = start - 1; i >= 0; i--) {
				if(comparator.compare(get(i), get(i + 1)) > 0) {
					Collections.swap(list, i, i + 1);
					modified = true;
				}
				else
					break;
			}
			status.increment();
		}
		status.setMessage("Sorted " + status.getCount() + " " + getPlural());
		if(modified) {
			renumber(status);
			graph.meta.set(MetaData.MODIFIED, Instant.now());
		}
	}
	
	/**
	 * {@link #sort(Comparator, Status) Sorts and renumbers} the symbols in this
	 * collection without reporting the method's progress while it runs.
	 * 
	 * @param comparator the comparator that defines the new order of elements
	 * @see #sort(Comparator, Status)
	 */
	public void sort(Comparator<? super S> comparator) {
		sort(comparator, new Status());
	}
	
	@Override
	protected boolean prune(Predicate<Object> predicate, Status status) {
		status.set("Pruning " + getPlural(), (long) size());
		boolean modified = false;
		for(S symbol : this) {
			symbol.prune(predicate);
			if(symbol.getID() == Settings.PRUNED)
				modified = true;
			status.increment();
		}
		status.setMessage("Pruned " + status.getCount() + " " + getPlural());
		return modified;
	}
	
	@Override
	protected void renumber(Status status) {
		status.set("Renumbering " + getPlural(), (long) size());
		int nextID = 0;
		Iterator<S> iterator = list.iterator();
		while(iterator.hasNext()) {
			S symbol = iterator.next();
			if(symbol.getID() == Settings.PRUNED) {
				iterator.remove();
				map.remove(symbol.name);
			}
			else
				symbol.setID(nextID++);
			status.increment();
		}
		graph.meta.set(getMetaDataKey(), size());
		status.setMessage("Renumbered " + status.getCount() + " " + getPlural());
	}
	
	@Override
	protected void read(GraphReader reader, Status status) throws IOException {
		super.read(reader, status);
		list.clear();
		map.clear();
		readSymbols(reader, status);
		readComments(reader, status);
	}
	
	/**
	 * Reads the {@link #getFileName() file} for this collection and {@link
	 * #add(String) creates} a new symbol for each line of the file.
	 * 
	 * @param reader a story graph reader
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while reading the file or if
	 * the file is not formatted correctly
	 */
	protected void readSymbols(GraphReader reader, Status status) throws IOException {
		if(reader.setFile(getFileName())) {
			super.read(reader, status);
			String name = reader.readNextLineAsString();
			while(name != null) {
				add(name);
				status.increment();
				name = reader.readNextLineAsString();
			}
			status.setMessage("Read " + status.getCount() + " " + getPlural());
		}
	}
	
	/*
	@Override
	protected void readComments(GraphReader reader, Status status) throws IOException {
		super.readComments(reader, status);
	}
	*/
	
	@Override
	protected void write(GraphWriter writer, Status status) throws IOException {
		writeSymbols(writer, status);
		writeComments(writer, status);
	}
	
	/**
	 * Writes the symbols in this collection to {@link #getFileName() file}.
	 * 
	 * @param writer a story graph writer
	 * @param status a status object that will be updated while this method runs
	 * to reflect its current progress
	 * @throws IOException if an exception occurs while writing the file
	 */
	protected void writeSymbols(GraphWriter writer, Status status) throws IOException {
		super.write(writer, status);
		for(S symbol : this) {
			writer.writeNextLineAsString(symbol.name);
			status.increment();
		}
		status.setMessage("Wrote " + status.getCount() + " " + getPlural());
	}
	
	/*
	@Override
	protected void writeComments(GraphWriter writer, Status status) throws IOException {
		super.writeComments(writer, status);
	}
	*/
}