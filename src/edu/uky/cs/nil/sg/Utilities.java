package edu.uky.cs.nil.sg;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;

/**
 * Common utility functions needed by multiple classes.
 * 
 * @author Stephen G. Ware
 */
public class Utilities {
	
	/**
	 * Checks whether two objects are {@link Object#equals(Object) equal}, even
	 * if one or both objects is {@code null}.
	 * 
	 * @param o1 the first object, or {@code null}
	 * @param o2 the second object, or {@code null}
	 * @return true if both object or {@code null} or if both objects are not
	 * {@code null} and are {@link Object#equals(Object) equal}
	 */
	public static final boolean equals(Object o1, Object o2) {
		if(o1 == null)
			return o2 == null;
		else
			return o2 != null && o1.equals(o2);
	}
	
	/**
	 * Returns the {@link Object#hashCode() hash code} of an object even if it
	 * is {@code null}.
	 * 
	 * @param object the object, or {@code null}
	 * @return the {@link Object#hashCode() hash code} of the object or 0 if it
	 * is {@code null}
	 */
	public static final int hashCode(Object object) {
		if(object == null)
			return 0;
		else
			return object.hashCode();
	}
	
	/**
	 * Returns the result of {@link Comparable#compareTo(Object) comparing} two
	 * comparable objects, even if one of both object is null. Null objects are
	 * ordered after non-null objects.
	 * 
	 * @param <C> the type of comparable object
	 * @param c1 the first object
	 * @param c2 the second object
	 * @return the result of comparing the objects, with null objects ordered
	 * after non-null
	 */
	public static final <C extends Comparable<C>> int compare(C c1, C c2) {
		if(c1 == null) {
			if(c2 == null)
				return 0;
			else
				return 1;
		}
		else if(c2 == null)
			return -1;
		else
			return c1.compareTo(c2);
	}
	
	/**
	 * Converts all elements in an {@link Iterable iterable} to an array of a
	 * given type.
	 * 
	 * @param <T> the type of the array to be returned
	 * @param iterable the collection of objects to the put into the array
	 * @param type the class object for the array's type
	 * @return an array of the given type containing all elements from the
	 * iterable
	 */
	public static final <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
		return toArray(iterable.iterator(), type, 0);
	}
	
	@SuppressWarnings("unchecked")
	private static final <T> T[] toArray(Iterator<? extends T> iterator, Class<T> type, int size) {
		if(iterator.hasNext()) {
			T element = iterator.next();
			T[] array = toArray(iterator, type, size + 1);
			array[size] = element;
			return array;
		}
		else
			return (T[]) Array.newInstance(type, size);
	}
	
	/**
	 * Returns the number of elements in an {@link Iterable iterable}.
	 * 
	 * @param iterable the iterable whose size should be counted
	 * @return the number of elements in the iterable
	 */
	public static final int size(Iterable<?> iterable) {
		int size = 0;
		Iterator<?> iterator = iterable.iterator();
		while(iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}
	
	/**
	 * Checks whether an iterable contains an element.
	 * 
	 * @param <T> the type of element in the iterable
	 * @param iterable the iterable
	 * @param element the element to search for
	 * @return true if the element occurs in the iterable, false otherwise
	 */
	public static final <T> boolean contains(Iterable<T> iterable, T element) {
		for(T member : iterable)
			if(equals(member, element))
				return true;
		return false;
	}
	
	/**
	 * Returns the element at the given index from an iterable. The first
	 * element in the iterable has index 0, the second has index 1, etc.
	 * 
	 * @param <T> the type of element in the iterable
	 * @param iterable the iterable
	 * @param index the index of the desired element
	 * @return the element at that index
	 * @throws IndexOutOfBoundsException if the index if less than 0 or if the
	 * index is greater than or equal to the number of elements in the iterable
	 */
	public static final <T> T get(Iterable<T> iterable, int index) {
		if(index < 0)
			throw Exceptions.indexOutOfBounds(index, size(iterable));
		Iterator<T> iterator = iterable.iterator();
		for(int i = 0; i < index && iterator.hasNext(); i++) {
			T element = iterator.next();
			if(i == index)
				return element;
		}
		throw Exceptions.indexOutOfBounds(index, size(iterable));
	}
	
	/**
	 * Returns the ratio between two long values as a percentage (between 0 and
	 * 100) rounded to a given number of decimal places. Values are rounded
	 * {@link RoundingMode#HALF_UP half up}, so 95.4% rounded to 0 decimal
	 * places would be 95%, but 95.6% would be 96%.
	 * 
	 * @param numerator the numerator of the fraction
	 * @param denominator the denominator of the fraction
	 * @param places the number of decimal places to include in the percentage
	 * @return the ratio as a percentage rounded to the given number of decimal
	 * places
	 */
	public static final double percent(long numerator, long denominator, int places) {
		if(denominator == 0)
			return Double.NaN;
		BigDecimal n = BigDecimal.valueOf(numerator);
		BigDecimal d = BigDecimal.valueOf(denominator);
		BigDecimal c = BigDecimal.valueOf(100);
		BigDecimal p = n.multiply(c).divide(d, RoundingMode.HALF_UP);
		return p.setScale(places, RoundingMode.HALF_UP).doubleValue();
	}
	
	/**
	 * Capitalizes each word in a string, meaning that the first character will
	 * be replaced by the same letter in {@link String#toUpperCase() upper
	 * case}. The first sequence of characters in a string is the first word,
	 * and each {@link java.lang.Character#isWhitespace(char) whitespace}
	 * character begins a new word.
	 * 
	 * @param string the strong to capitalize
	 * @return the string with each word capitalized
	 */
	public static final String capitalize(String string) {
		if(string.length() > 0) {
			string = string.substring(0, 1).toUpperCase() + string.substring(1);
			for(int i=1; i<string.length()-1; i++)
				if(java.lang.Character.isWhitespace(string.charAt(i)))
					string = string.substring(0, i + 1) + string.substring(i + 1, i + 2).toUpperCase() + string.substring(i + 2);
			return string;
		}
		else
			return string;
	}
	
	/**
	 * Wraps a string in double quote ({@code "}) characters and escapes all
	 * new line, carriage return, and double quote characters with {@code \\n},
	 * {@code \\r}, and {@code \\\"} respectively.
	 * 
	 * @param string any string
	 * @return the string wrapped in quotes with special characters escaped
	 * @see #unquote(String)
	 */
	public static final String quote(String string) {
		string = string.replace("\n", "\\n");
		string = string.replace("\r", "\\r");
		string = string.replace("\"", "\\\"");
		return "\"" + string + "\"";
	}
	
	/**
	 * Removes double quote characters around a string, if they exist, and
	 * replaces any escaped new line, carriage return, and double quote
	 * characters with their unescaped versions.
	 * 
	 * @param string any string
	 * @return the string without its wrapping quotes and with escaped
	 * special characters replaced with their unescaped versions
	 * @see #quote(String)
	 */
	public static final String unquote(String string) {
		if(string.startsWith("\"") && string.endsWith("\"") && string.length() >= 2) {
			string = string.substring(1, string.length() - 1);
			string = string.replace("\\n", "\n");
			string = string.replace("\\r", "\r");
			string = string.replace("\\\"", "\"");
			return string;
		}
		else
			return string;
	}
	
	/**
	 * {@link Integer#parseInt(String) Parses} a string as an integer and
	 * returns the value or throws an exception of the string cannot be parsed.
	 * 
	 * @param string the string to be parsed as an integer
	 * @return the integer
	 * @throws NumberFormatException if the string cannot be parsed
	 */
	public static final int toInteger(String string) {
		try {
			return Integer.parseInt(string);
		}
		catch(NumberFormatException exception) {
			throw new NumberFormatException(Exceptions.cannotConvert(string, "a Java integer").getMessage());
		}
	}
	
	/**
	 * {@link Long#parseLong(String) Parses} a string as an long and returns the
	 * value or throws an exception of the string cannot be parsed.
	 * 
	 * @param string the string to be parsed as a long
	 * @return the long
	 * @throws NumberFormatException if the string cannot be parsed
	 */
	public static final long toLong(String string) {
		try {
			return Long.parseLong(string);
		}
		catch(NumberFormatException exception) {
			throw new NumberFormatException(Exceptions.cannotConvert(string, "a Java long").getMessage());
		}
	}
}