/**
 * ============================================================
 *
 * This file is part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb.patterns;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class provides an implementation
 * of Java's Future interface for use
 * with request invocations.
 *
 * @author jschaefe
 * @author swrede
 *
 * @see java.util.concurrent.Future
 */

public class Future<T> implements java.util.concurrent.Future<T> {

	protected Throwable exception = null;
	protected T result = null;
	protected boolean cancelled = false;

	public synchronized void complete(final T val) {
		result = val;
		notifyAll();
	}

	public synchronized void error(final Throwable exception) {
		this.exception = exception;
		notifyAll();
	}

	/**
	 * This method makes the waiting thread return with a CancellationException
	 * but does not cancel the actual operation the thread was waiting for.
	 * @param mayInterruptIfRunning
	 * @return false
	 */
	public boolean cancel(boolean mayInterrupt) {
		cancelled = true;
		if(mayInterrupt) {
			notifyAll();
		}
		return true;
	}

	/**
	 * Convenience method for get(0, TimeUnit.MILLISECONDS).
	 * @see Future#get(long, TimeUnit)
	 */
	public synchronized T get() throws InterruptedException, ExecutionException {
		try {
			return get(0, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			// this is not going to happen
			assert(false);
			return null;
		}
	}

	/**
	 * Convenience method for get(timeout, TimeUnit.MILLISECONDS).
	 * @see Future#get(long, TimeUnit)
	 */
	public synchronized T get(final long timeout) throws InterruptedException, ExecutionException, TimeoutException {
		return get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * Gets the results passed to this callback object. This method blocks until
	 * either the results are available, or the timeout is reached.
	 *
	 * @param timeout number of TimeUnits to wait for results to become
	 * available
	 * @param unit TimeUnit to use
	 * @return the value resulting from the operation
	 *
	 * @see Future#get(long, TimeUnit)
	 * @throws InterruptedException if the calling thread is interrupted while
	 * waiting
	 * @throws ExecutionException if the operation resulted in an Exception
	 * @throws TimeoutException if the timeout was reached before results were
	 * available
	 */
	public synchronized T get(final long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long timeout_millis = unit.toMillis(timeout);
		// prevent waiting forever, if timeout was not 0, but was rounded to 0
		if (timeout > 0 && timeout_millis == 0) {
			timeout_millis = 1;
		}
		while (result == null && exception == null) {
			wait(timeout_millis);
		}
		if (exception != null) { // operation threw an exception
			throw new ExecutionException(exception);
		} else if (isCancelled()) { // cancel() was called before
			throw new CancellationException(
					"async operation was cancelled before it completed");
		} else if (result == null) { // no result yet, timeout was hit
			throw new TimeoutException();
		}
		return result;
	}

	/**
	 * Always returns false since operation can not be cancelled
	 * @return false
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Checks whether results are already available.
	 * @return true if results have already been passed to this callback
	 */
	public synchronized boolean isDone() {
		return result != null || exception != null;
	}

}
