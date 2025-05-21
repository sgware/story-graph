package edu.uky.cs.nil.sg;

/**
 * A story graph element that can have a {@code String} comment associated with
 * it.
 */
public interface Commented {
	
	/**
	 * Returns the comment associated with this element.
	 * 
	 * @return the comment, or null if the element has no comment
	 */
	public String getComment();
	
	/**
	 * Sets the comment associated with this element.
	 * 
	 * @param comment the new comment
	 */
	public void setComment(String comment);
}