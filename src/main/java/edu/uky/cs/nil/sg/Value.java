package edu.uky.cs.nil.sg;

/**
 * A value is anything that can be assigned to a {@link Fluent fluent} in a
 * {@link State state}.
 * 
 * @author Stephen G. Ware
 */
public interface Value extends Comparable<Value> {
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * When comparing values of different types, {@link NominalValue nominal
	 * values} always come first and {@link NumericValue numeric values} always
	 * come last.
	 */
	@Override
	public int compareTo(Value other);
	
	/**
	 * Converts this value to a Java Boolean.
	 * 
	 * @return this value after being converted to a Boolean
	 * @throws IllegalArgumentException if the value cannot be converted to a
	 * Boolean
	 */
	public default boolean toBoolean() {
		throw Exceptions.cannotConvert(toString(), "a boolean");
	}
	
	/**
	 * Converts this value to a Java integer.
	 * 
	 * @return this value after being converted to an integer
	 * @throws IllegalArgumentException if the value cannot be converted to an
	 * integer
	 */
	public default int toInteger() {
		throw Exceptions.cannotConvert(toString(), "an integer");
	}
	
	/**
	 * Converts this value to a Java long integer.
	 * 
	 * @return this value after being converted to a long
	 * @throws IllegalArgumentException if the value cannot be converted to a
	 * long
	 */
	public default long toLong() {
		throw Exceptions.cannotConvert(toString(), "a long");
	}
	
	/**
	 * Converts this value to a Java floating point number.
	 * 
	 * @return this value after being converted to a float
	 * @throws IllegalArgumentException if the value cannot be converted to a
	 * float
	 */
	public default float toFloat() {
		throw Exceptions.cannotConvert(toString(), "a float");
	}
	
	/**
	 * Converts this value to a Java double precision floating point number.
	 * 
	 * @return this value after being converted to a double
	 * @throws IllegalArgumentException if the value cannot be converted to a
	 * double
	 */
	public default double toDouble() {
		throw Exceptions.cannotConvert(toString(), "a double");
	}
}