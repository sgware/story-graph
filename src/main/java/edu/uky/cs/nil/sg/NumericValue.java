package edu.uky.cs.nil.sg;

import java.util.HashMap;
import java.util.Map;

/**
 * Numeric {@link Value values} represent numbers and are singleton objects that
 * exist in all {@link StoryGraph story graphs} without needing to be explicitly
 * defined in the {@link StoryGraph#values value list}.
 * 
 * @author Stephen G. Ware
 */
public class NumericValue implements Value {
	
	/** Stores all singleton objects that have been created so far */
	private static final Map<Double, NumericValue> singletons = new HashMap<>();
	
	/**
	 * Converts the given object into a singleton numeric value, if possible.
	 * Null is converted into {@link Double#NaN}. The Java {@link Number number}
	 * classes {@link Integer} and {@link Double} are converted via their {@link
	 * Number#doubleValue()} method. Other objects are converted by attempting
	 * to {@link Double#parseDouble(String) parse} the output of their {@link
	 * Object#toString() toString()} method as a double.
	 * <p>
	 * Numeric values are singletons, meaning that if this method is called more
	 * than once with an input that would be converted to the same number then
	 * the same numeric value object is returned every time.
	 * 
	 * @param object the object to be converted to a numeric value
	 * @return a singleton numeric value
	 * @throws IllegalArgumentException if the value cannot be converted to a
	 * numeric value
	 */
	public static final NumericValue get(Object object) {
		if(object == null)
			return get(Double.NaN);
		else if(object instanceof Integer || object instanceof Double)
			return get(((Number) object).doubleValue());
		else {
			try {
				return get(Double.parseDouble(object.toString()));
			}
			catch(NumberFormatException exception) {
				throw Exceptions.cannotConvert(object.toString(), "a double");
			}
		}
	}
	
	/**
	 * Converts the given number into a singleton numeric value object.
	 * <p>
	 * Numeric values are singletons, meaning that if this method is called more
	 * than once with the same value then  the same numeric value object is
	 * returned every time.
	 * 
	 * @param value the value to be converted into a numeric value object
	 * @return a singleton numeric value object
	 */
	public static final NumericValue get(double value) {
		NumericValue singleton = singletons.get(value);
		if(singleton == null) {
			singleton = new NumericValue(value);
			singletons.put(value, singleton);
		}
		return singleton;
	}
	
	/** The numeric value of this object as a Java {@code double} */
	public final double value;
	
	/**
	 * Constructs a new numeric value object for the given value.
	 * 
	 * @param value the numeric value object's value
	 */
	NumericValue(double value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}
	
	@Override
	public String toString() {
		return Double.toString(value);
	}
	
	@Override
	public int compareTo(Value other) {
		if(other instanceof NumericValue otherNV)
			return Double.compare(this.value, otherNV.value);
		else
			return 1;
	}
	
	@Override
	public int toInteger() {
		if(value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE && value % 1 == 0)
			return ((Double) value).intValue();
		else
			return Value.super.toInteger();
	}
	
	@Override
	public long toLong() {
		if(value % 1 == 0) {
			long asLong = (long) value;
			double asDouble = (double) asLong;
			if(value == asDouble)
				return asLong;
		}
		return Value.super.toLong();
	}
	
	@Override
	public float toFloat() {
		if(Double.isNaN(value))
			return Float.NaN;
		else if(Double.isInfinite(value)) {
			if(value < 0)
				return Float.NEGATIVE_INFINITY;
			else
				return Float.POSITIVE_INFINITY;
		}
		else {
			float asFloat = (float) value;
			double asDouble = (double) asFloat;
			if(value == asDouble)
				return asFloat;
			return Value.super.toFloat();
		}
	}
	
	@Override
	public double toDouble() {
		return value;
	}
}