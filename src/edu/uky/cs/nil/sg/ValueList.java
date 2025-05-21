package edu.uky.cs.nil.sg;

import java.util.function.Predicate;

/**
 * The {@link SymbolList list} of {@link NominalValue nominal values} in a
 * {@link StoryGraph story graph}. {@link Value Values} are assigned to {@link
 * Fluent fluents} in {@link Node nodes}. Nominal values are values that can be
 * one of several possible objects. {@link NumericValue Numeric values} are
 * defined as {@link NumericValue#get(double) singleton objects} and are not
 * defined in this list.
 * 
 * @author Stephen G. Ware
 */
public class ValueList extends SymbolList<NominalValue> {
	
	/**
	 * Constructs a new nominal values list for the given story graph.
	 * 
	 * @param graph the story graph to which this list's values belong
	 */
	protected ValueList(StoryGraph graph) {
		super(graph);
	}
	
	@Override
	protected String getMetaDataKey() {
		return MetaData.VALUES;
	}
	
	@Override
	protected String getSingular() {
		return "value";
	}
	
	@Override
	public NominalValue add(String name) {
		NominalValue value = get(name);
		if(value == null) {
			if(name == null)
				throw Exceptions.cannotBeNull(getSingular() + " name");
			value = new NominalValue(size(), name);
			add(value);
		}
		return value;
	}
	
	@Override
	public boolean remove(Predicate<? super NominalValue> predicate, Status status) {
		return removeAndPrune(
			predicate,
			status,
			graph.states
		);
	}
}