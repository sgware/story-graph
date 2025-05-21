package edu.uky.cs.nil.sg;

import java.util.Comparator;
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
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If this method reorders any of the fluents in this collection, it will
	 * also modify all of the {@link State#getValue(Fluent) fluent values} in
	 * the graph's {@link StoryGraph#states state objects} to reflect the new
	 * order of fluents, which can take a long time if there are many state
	 * objects.
	 */
	@Override
	public boolean sort(Comparator<? super Fluent> comparator, Status status) {
		Fluent[] original = new Fluent[size()];
		for(Fluent fluent : this)
			original[fluent.getID()] = new Fluent(fluent.getID(), fluent.name);
		if(super.sort(comparator, status)) {
			status.set("Reordering state fluent values", graph.states.size());
			Fluent[] reordered = new Fluent[original.length];
			for(Fluent fluent : original)
				reordered[get(fluent.name).getID()] = fluent;
			for(State state : graph.states) {
				Value[] values = new Value[reordered.length];
				for(int i = 0; i < reordered.length; i++)
					values[i] = state.getValue(reordered[i]);
				state.setValues(values);
				status.increment();
			}
			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean remove(Predicate<? super Fluent> predicate, Status status) {
		return removeAndPrune(
			predicate,
			status,
			graph.states
		);
	}
}