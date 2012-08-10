package rsb.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A limited capacity BlockingQueue which overrides add in order to remove
 * the oldest element when the size limit is reached.
 * 
 * @author jwienke
 * 
 * @param <T>
 *            contained element type
 */
public class LimitedQueue<T> implements BlockingQueue<T> {

	protected BlockingQueue<T> queue;

	public LimitedQueue(final int capacity) {
		queue = new LinkedBlockingQueue<T>(capacity);
	}

	@Override
	public synchronized T remove() {
		return queue.remove();
	}

	@Override
	public synchronized T poll() {
		return queue.poll();
	}

	@Override
	public synchronized T element() {
		return queue.element();
	}

	@Override
	public synchronized T peek() {
		return queue.peek();
	}

	@Override
	public synchronized int size() {
		return queue.size();
	}

	@Override
	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public synchronized Iterator<T> iterator() {
		return queue.iterator();
	}

	@Override
	public synchronized Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public synchronized <U> U[] toArray(U[] a) {
		return queue.toArray(a);
	}

	@Override
	public synchronized boolean containsAll(Collection<?> c) {
		return queue.containsAll(c);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends T> c) {
		return queue.addAll(c);
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) {
		return queue.removeAll(c);
	}

	@Override
	public synchronized boolean retainAll(Collection<?> c) {
		return queue.retainAll(c);
	}

	@Override
	public synchronized void clear() {
		queue.clear();

	}

	@Override
	public boolean add(T e) {
		if (queue.remainingCapacity() == 0) {
			queue.poll();
		}
		return queue.add(e);
	}

	@Override
	public boolean offer(T e) {
		return queue.offer(e);
	}

	@Override
	public void put(T e) throws InterruptedException {
		queue.put(e);

	}

	@Override
	public boolean offer(T e, long timeout, TimeUnit unit)
			throws InterruptedException {
		return queue.offer(e, timeout, unit);
	}

	@Override
	public T take() throws InterruptedException {
		return queue.take();
	}

	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		return queue.poll(timeout, unit);
	}

	@Override
	public int remainingCapacity() {
		return queue.remainingCapacity();
	}

	@Override
	public boolean remove(Object o) {
		return queue.remove(o);
	}

	@Override
	public boolean contains(Object o) {
		return queue.contains(o);
	}

	@Override
	public int drainTo(Collection<? super T> c) {
		return queue.drainTo(c);
	}

	@Override
	public int drainTo(Collection<? super T> c, int maxElements) {
		return queue.drainTo(c, maxElements);
	}

}
