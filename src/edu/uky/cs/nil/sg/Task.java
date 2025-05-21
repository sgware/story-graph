package edu.uky.cs.nil.sg;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A task is any process that may take a long time to run and which can update
 * a {@link Status status} object to reflect its current progress. A task only
 * implements the {@link #run(Status)} method. A task can be executed by calling
 * its {@link #run(Status) run} method directly, but when it is executed using
 * one of the static {@link #run(Task, Status, boolean, int, TimeUnit) run
 * methods}, it will be executed on a different thread and the status object
 * being updated by the task is periodically {@link Status#toString() printed}
 * to {@link System#out} at a specified frequency.
 * 
 * @author Stephen G. Ware
 */
@FunctionalInterface
public interface Task {
	
	/** By default, a task prints every 1 unit of time */
	public static final int DEFAULT_FREQUENCY = 1;
	
	/** By default, a task uses seconds as its time unit */
	public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
	
	/**
	 * Run a task on a separate thread but {@link Status#toString() print} its
	 * status to {@link System#out} at a given frequency. If the status prints
	 * an update at least once while it is running, it will also print again
	 * when it completes. If the {@code print} parameter is true then the status
	 * will always print its update when it completes.
	 * 
	 * @param task the task to run on a separate thread
	 * @param status the status object the task object will update as it runs
	 * @param print whether the status should print at least one time
	 * @param frequency the number of time units that will pass between printing
	 * updates
	 * @param unit the time unit that must pass some number of times between
	 * printing updates
	 * @throws Exception if an exception occurs while the task is running
	 */
	public static void run(Task task, Status status, boolean print, int frequency, TimeUnit unit) throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Object> future = executor.submit(() -> {
			task.run(status);
			return null;
		});
		Throwable problem = null;
		int pad = 0;
		boolean hasPrinted = false;
		do {
			try {
				future.get(frequency, unit);
			}
			catch(TimeoutException exception) {
				String string = status.toString();
				pad = Math.max(pad, string.length());
				System.out.println("\r" + String.format("%-" + pad + "s", string));
				hasPrinted = true;
			}
			catch(CancellationException | InterruptedException exception) {
				problem = exception;
				break;
			}
			catch(ExecutionException exception) {
				problem = exception.getCause();
				break;
			}
		}
		while(!future.isDone());
		executor.shutdown();
		if(problem instanceof Error)
			throw (Error) problem;
		else if(problem != null)
			throw (Exception) problem;
		if(print || hasPrinted) {
			String string = status.toString();
			pad = Math.max(pad, string.length());
			System.out.println((hasPrinted ? "\r" : "") + String.format("%-" + pad + "s", string));
		}
	}
	
	/**
	 * Run a task on a separate thread but {@link Status#toString() print} its
	 * status at a given frequency.
	 * 
	 * @param task the task to run on a separate thread
	 * @param print whether the status should print at least one time
	 * @param frequency the number of time units that will pass between printing
	 * updates
	 * @param unit the time unit that must pass some number of times between
	 * printing updates
	 * @throws Exception if an exception occurs while the task is running
	 * @see #run(Task, Status, boolean, int, TimeUnit)
	 */
	public static void run(Task task, boolean print, int frequency, TimeUnit unit) throws Exception {
		run(task, new Status(), print, frequency, unit);
	}
	
	/**
	 * Run a task on a separate thread but {@link Status#toString() print} its
	 * status at the default frequency.
	 * 
	 * @param task the task to run on a separate thread
	 * @param status the status object the task object will update as it runs
	 * @param print whether the status should print at least one time
	 * @throws Exception if an exception occurs while the task is running
	 * @see #run(Task, Status, boolean, int, TimeUnit)
	 */
	public static void run(Task task, Status status, boolean print) throws Exception {
		run(task, status, print, DEFAULT_FREQUENCY, DEFAULT_TIME_UNIT);
	}
	
	/**
	 * Run a task on a separate thread but {@link Status#toString() print} its
	 * status at the default frequency.
	 * 
	 * @param task the task to run on a separate thread
	 * @param status the status object the task object will update as it runs
	 * @throws Exception if an exception occurs while the task is running
	 * @see #run(Task, Status, boolean, int, TimeUnit)
	 */
	public static void run(Task task, Status status) throws Exception {
		run(task, status, false, DEFAULT_FREQUENCY, DEFAULT_TIME_UNIT);
	}
	
	/**
	 * Run a task on a separate thread but {@link Status#toString() print} its
	 * status at the default frequency.
	 * 
	 * @param task the task to run on a separate thread
	 * @throws Exception if an exception occurs while the task is running
	 */
	public static void run(Task task) throws Exception {
		run(task, new Status());
	}
	
	/**
	 * Execute a process which may take a long time but will update a given
	 * {@link Status status} object to reflect its current progress.
	 * 
	 * @param status the status object the task will update as it runs
	 * @throws Exception if an exception occurs while the process is running
	 */
	public void run(Status status) throws Exception;
}