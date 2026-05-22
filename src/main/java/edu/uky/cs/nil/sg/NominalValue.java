package edu.uky.cs.nil.sg;

/**
 * A nominal {@link Value value} is a {@link StoryGraph story graph} {@link
 * Symbol symbol} that can be assigned to a {@link Fluent} in a {@link Node
 * node's} {@link State state}.
 * <p>
 * New nominal values should be created in a story graph using the {@link
 * ValueList#add(String)} method.
 */
public class NominalValue extends Symbol implements Value {
	
	/**
	 * Constructs a new nominal value symbol with the given ID and name.
	 * 
	 * @param id the nominal value's unique sequential ID number
	 * @param name the nominal value's unique name
	 */
	protected NominalValue(int id, String name) {
		super(id, name);
	}
	
	@Override
	public int compareTo(Value other) {
		if(other instanceof NominalValue otherNV)
			return Integer.compare(this.getID(), otherNV.getID());
		else
			return -1;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If this value's {@link #name name}, when converted to upper case, is
	 * {@code "T"} or {@code "TRUE"}, this method returns Boolean {@code true}.
	 * If this value's {@link #name name}, when converted to upper case, is
	 * {@code "F"} or {@code "FALSE"}, this method returns Boolean {@code
	 * false}. Otherwise, this method throws an exception.
	 */
	@Override
	public boolean toBoolean() {
		String string = toString().toUpperCase();
		if(string.equals("T") || string.equals("TRUE"))
			return true;
		else if(string.equals("F") || string.equals("FALSE"))
			return false;
		else
			return Value.super.toBoolean();
	}
}