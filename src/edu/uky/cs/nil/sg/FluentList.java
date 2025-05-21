package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * The {@link SymbolList list} of {@link Fluent fluent}, or state variables,
 * in a {@link StoryGraph story graph}. Every fluent has a {@link Value value}
 * in a {@link Node node}.
 * 
 * @author Stephen G. Ware
 */
public class FluentList extends SymbolList<Fluent> {
	
	/**
	 * Constructs a new fluent list for the given story graph.
	 * 
	 * @param graph the story graph to which this list's characters belong
	 */
	protected FluentList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.FLUENTS;
	}
	
	@Override
	protected String getSingular() {
		return "fluent";
	}
	
	@Override
	public Fluent add(String name) {
		Fluent fluent = get(name);
		if(fluent == null) {
			if(name == null)
				throw Exceptions.cannotBeNull(getSingular() + " name");
			fluent = new Fluent(size(), name);
			add(fluent);
		}
		return fluent;
	}
	
	@Override
	public void remove(Predicate<? super Fluent> predicate, Status status) {
		removeAndPrune(
			predicate,
			status,
			graph.states
		);
	}
}