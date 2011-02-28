/**
 * 
 */
package rsb.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class BasicSynchronizedQueue<T1,T2> {
	
	private final static Logger log = Logger.getLogger(BasicSynchronizedQueue.class.getName());	
	// TODO add unit tests
	// TODO change to java.util.concurrent
	// TODO Think about meaning of close
	//        - throw Exception on push() if closed?
	//        - return null from next() if closed?
	
	Deque<T1> queue;
	boolean closed = false;
	
	public synchronized boolean isClosed() {
		return closed;
	}

	public synchronized int getSize() {
		return queue.size();
	}
	
	public BasicSynchronizedQueue() {
		queue = new ArrayDeque<T1>();		
	}
	
	public synchronized void push(T2 element) {
		if (closed) {
			throw new QueueClosedException("Queue is closed!");
		}
		T1 e = convert(element);
		if (e!=null) {
			queue.add(e);
			notifyAll(); 
		}		
	}	
	
	@SuppressWarnings("unchecked")
	public T1 convert(T2 e) {
		return (T1) e; 
	}
	
	public synchronized T1 pop() {
		// remove and return head element
		Iterator<T1> e = queue.iterator();
		if (e.hasNext()) {
			T1 elm = e.next();
			e.remove();
			return elm;
		}
		if (closed) {
			throw new QueueClosedException("Queue is closed!");
		}
		return null;
	}
	
	public synchronized boolean hasNext() {
		if (!queue.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public synchronized T1 next() throws InterruptedException {
		return next(-1);
	}
	
	public synchronized T1 next(long timeout) throws InterruptedException {
		if (!closed && !queue.isEmpty()) {
			// wait for new element to be inserted
			try {
				if (timeout==-1) {
					this.wait();
				} else {
					this.wait(timeout);
				}
			} catch (java.lang.InterruptedException ex) {
				// thread was interrupted while in wait
				throw new InterruptedException(ex.getMessage());
			}
			return pop();
		} else {
			// directly return head element
			return pop();
		}
	}
	
	public synchronized void close() {	
		closed = true;
		notifyAll();
	}

}
