package edu.uky.cs.nil.sg;

/**
 * An object that can be periodically updated to reflect the progress of a
 * long-running operation. The current status can be read using the {@link
 * #toString()} method.
 * <p>
 * A status has three parts: {@link #getMessage() a message}, {@link
 * #getCount() a count}, and {@link #getTotal() a total}. The count and total
 * are optional. When a new message is set, the count and total are
 * automatically set to 0.
 * <p>
 * Updating the message, count, and total are inexpensive operations and can be
 * called many times without significantly impacting performance.
 * 
 * @author Stephen G. Ware
 */
public class Status {
	
	/** The status message */
	private String message;
	
	/** The count */
	private long count = 0;
	
	/** The total */
	private long total = 0;
	
	/**
	 * Constructs a new status.
	 */
	public Status() {
		setMessage(null);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the {@link #getCount()} is 0, this method simply returns {@link
	 * #getMessage() the message}. If the count is greater than 0, this method
	 * returns the message followed by a colon, followed by the current count.
	 * If the total is greater than 0, the count is followed by a forward slash
	 * and the total and then by the ratio of the count to that total as a
	 * percentage in parentheses.
	 * <p>
	 * For example, if the message is {@code "Checking elements"} and the count is
	 * 0, this method returns {@code "Checking elements"}.
	 * <p>
	 * If the count is 5 and the total is 0, this method returns {@code
	 * "Checking elements: 5"}.
	 * <p>
	 * If the count is 5 and the total is 10, this method returns {@code
	 * "Checking elements: 5/10 (50%)"}.
	 */
	@Override
	public String toString() {
		String message = getMessage();
		long count = getCount();
		long total = getTotal();
		if(count > 0) {
			if(total > 0)
				message += ": " + count + "/" + total + " (" + percent(count, total) + "%)";
			else
				message += ": " + count;
		}
		return message;
	}
	
	private static final int percent(long count, long total) {
		if(total == 0)
			return 0;
		else if(count == total)
			return 100;
		else
			return (int) Math.min(Utilities.percent(count, total, 0), 99);
	}
	
	/**
	 * Returns the status's current message. This returns only the message, not
	 * the full status, which is returned by {@link #toString()}.
	 * 
	 * @return the message
	 * @see #toString()
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the current message. This sets only the message part of the status,
	 * not the full status, which is returned by {@link #toString()}. This
	 * method also sets the {@link #getCount() count} and {@link #getTotal()
	 * total} to 0.
	 * 
	 * @param message the new message
	 * @see #toString()
	 */
	public void setMessage(String message) {
		if(message == null)
			message = "working...";
		this.message = message;
		setCount(0);
		setTotal(null);
	}
	
	/**
	 * Returns the status's current count.
	 * 
	 * @return the count
	 * @see #toString()
	 */
	public long getCount() {
		return count;
	}
	
	/**
	 * Sets the status's current count. If the value is less than 0, the count
	 * will be set to 0.
	 * 
	 * @param count the new count
	 * @see #toString()
	 */
	public void setCount(long count) {
		this.count = Math.max(count, 0);
	}
	
	/**
	 * Adds 1 to the {@link #getCount() count}.
	 */
	public void increment() {
		setCount(getCount() + 1);
	}
	
	/**
	 * Returns the status's current total.
	 * 
	 * @return the total
	 * @see #toString()
	 */
	public long getTotal() {
		return total;
	}
	
	/**
	 * Sets the status's current total. If the value is {@code null} or less
	 * than 0, the total will be set to 0.
	 * 
	 * @param total the new total
	 * @see #toString()
	 */
	public void setTotal(Long total) {
		if(total == null)
			this.total = 0L;
		else
			this.total = Math.max(total, 0);
	}
	
	/**
	 * Sets both the {@link #setMessage(String) message} and the {@link
	 * #setTotal(Long) total}.
	 * 
	 * @param message the new message
	 * @param total the new total
	 * @see #setMessage(String)
	 * @see #setTotal(Long)
	 */
	public void set(String message, Long total) {
		setMessage(message);
		setTotal(total);
	}
}