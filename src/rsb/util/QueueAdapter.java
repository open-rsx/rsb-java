/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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
package rsb.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import rsb.AbstractDataHandler;

/**
 * Synchronized queue implementing the rsb.DataHandler interface. Can be
 * directly registered as handler in rsb.Listener instance and used for
 * receiving and storing dispatched events.
 * 
 * @author swrede
 * @author dklotz
 */
public class QueueAdapter<T> extends AbstractDataHandler<T> {
	BlockingQueue<T> queue;

	/**
	 * A limited capacity BlockingQueue which overrides add in order to remove
	 * the oldest element when the size limit is reached.
	 * 
	 * @author jwienke
	 * 
	 * @param <T>
	 *            contained element type
	 */
	private class LimitedQueue implements BlockingQueue<T> {

		private BlockingQueue<T> queue;

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

	/**
	 * Creates an adapter with a preset unlimited queue inside.
	 */
	public QueueAdapter() {
		queue = new LinkedBlockingDeque<T>();
	}

	/**
	 * Creates an adapter with a preset queue inside that is limited to
	 * <code>capacity</code> elements.
	 * 
	 * @param capacity
	 *            capacity of the internal queue
	 */
	public QueueAdapter(final int capacity, final boolean discardOldest) {
		if (!discardOldest) {
			queue = new LinkedBlockingDeque<T>(capacity);
		} else {
			queue = new LimitedQueue(capacity);
		}
	}

	public QueueAdapter(BlockingQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public void handleEvent(T data) {
		queue.add(data);
	}

	public BlockingQueue<T> getQueue() {
		return queue;
	}
}
