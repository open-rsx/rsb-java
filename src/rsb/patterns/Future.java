/**
 * ============================================================
 *
 * This file is part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
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
 * @author jmoringe
 *
 * @see java.util.concurrent.Future
 */

public class Future<T> implements java.util.concurrent.Future<T> {

	protected Throwable exception = null;
	protected T result = null;

	protected boolean hasResult = false;
	protected boolean cancelled = false;

	public synchronized void complete(final T val) {
		result = val;
		hasResult = true;
		notifyAll();
	}

	public synchronized void error(final Throwable exception) {
		this.exception = exception;
		hasResult = true;
		notifyAll();
	}

	/**
	 * This method makes the waiting thread return with a
	 * CancellationException but does not cancel the actual
	 * operation the thread was waiting for.
	 *
	 * @param mayInterrupt
	 * @return false
	 */
	public synchronized boolean cancel(boolean mayInterrupt) {
		cancelled = true;
		if (mayInterrupt) {
		    notifyAll();
		}
		return true;
	}

	/**
	 * Convenience method for get(0, TimeUnit.MILLISECONDS).
	 * @see Future#get(long, TimeUnit)
	 */
	public synchronized T get() throws ExecutionException {
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
	public synchronized T get(final long timeout) throws ExecutionException, TimeoutException {
		return get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * Gets the results passed to this callback object. This
	 * method blocks until either the results are available, or
	 * the timeout is reached.
	 *
	 * @param timeout number of TimeUnits to wait for results to become
	 * available
	 * @param unit TimeUnit to use
	 * @return the value resulting from the operation
	 *
	 * @see Future#get(long, TimeUnit)
	 * @throws ExecutionException if the operation resulted in an Exception
	 * @throws TimeoutException if the timeout was reached before results were
	 * available
	 */
	public synchronized T get(final long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
		if (timeout == 0) {
		    // Wait until
		    // - a result arrives
		    // - the operation is cancelled
		    // In case of spurious wakeups, just continue waiting.
		    while (!hasResult && !cancelled) {
			try {
			    wait();
			} catch (InterruptedException e) {}
		    }
		} else {
		    // Calculate waiting time in milliseconds. Prevent
		    // waiting forever, if timeout was not 0, but was
		    // rounded to 0
		    long timeout_millis = unit.toMillis(timeout);
		    if (timeout > 0 && timeout_millis == 0) {
			timeout_millis = 1;
		    }

		    // Wait until
		    // - a result arrives
		    // - the operation is cancelled
		    // - the specified timeout is exceeded
		    // In case of spurious wakeups, just continue waiting.
		    long waitTime = 0;
		    while (!hasResult && !cancelled && (waitTime < timeout_millis)) {
			try {
			    wait(timeout_millis - waitTime);
			} catch (InterruptedException e) {}
			waitTime += timeout_millis; /* TODO(jmoringe): use real clock */
		    }
		}

		// One of the conditions occurred. Determine which.
		if (exception != null) { // operation threw an exception
		    throw new ExecutionException(exception);
		} else if (isCancelled()) { // cancel() was called before
		    throw new CancellationException("async operation was cancelled before it completed");
		} else if (!hasResult) { // no result yet, timeout was hit
		    throw new TimeoutException();
		}
		return result;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Checks whether results are already available.
	 * @return true if results have already been passed to this callback
	 */
	public synchronized boolean isDone() {
		return hasResult;
	}

}
