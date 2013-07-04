package rsb.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A limited capacity BlockingQueue which overrides add in order to remove the
 * oldest element when the size limit is reached.
 * 
 * @author jwienke
 * 
 * @param <T>
 *            contained element type
 */
public class LimitedQueue<T> implements BlockingQueue<T> {

    protected BlockingQueue<T> queue;

    public LimitedQueue(final int capacity) {
        this.queue = new LinkedBlockingQueue<T>(capacity);
    }

    @Override
    public synchronized T remove() {
        return this.queue.remove();
    }

    @Override
    public synchronized T poll() {
        return this.queue.poll();
    }

    @Override
    public synchronized T element() {
        return this.queue.element();
    }

    @Override
    public synchronized T peek() {
        return this.queue.peek();
    }

    @Override
    public synchronized int size() {
        return this.queue.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return this.queue.iterator();
    }

    @Override
    public synchronized Object[] toArray() {
        return this.queue.toArray();
    }

    @Override
    public synchronized <U> U[] toArray(final U[] a) {
        return this.queue.toArray(a);
    }

    @Override
    public synchronized boolean containsAll(final Collection<?> c) {
        return this.queue.containsAll(c);
    }

    @Override
    public synchronized boolean addAll(final Collection<? extends T> c) {
        return this.queue.addAll(c);
    }

    @Override
    public synchronized boolean removeAll(final Collection<?> c) {
        return this.queue.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(final Collection<?> c) {
        return this.queue.retainAll(c);
    }

    @Override
    public synchronized void clear() {
        this.queue.clear();

    }

    @Override
    public boolean add(final T e) {
        if (this.queue.remainingCapacity() == 0) {
            this.queue.poll();
        }
        return this.queue.add(e);
    }

    @Override
    public boolean offer(final T e) {
        return this.queue.offer(e);
    }

    @Override
    public void put(final T e) throws InterruptedException {
        this.queue.put(e);

    }

    @Override
    public boolean offer(final T e, final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return this.queue.offer(e, timeout, unit);
    }

    @Override
    public T take() throws InterruptedException {
        return this.queue.take();
    }

    @Override
    public T poll(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return this.queue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return this.queue.remainingCapacity();
    }

    @Override
    public boolean remove(final Object o) {
        return this.queue.remove(o);
    }

    @Override
    public boolean contains(final Object o) {
        return this.queue.contains(o);
    }

    @Override
    public int drainTo(final Collection<? super T> c) {
        return this.queue.drainTo(c);
    }

    @Override
    public int drainTo(final Collection<? super T> c, final int maxElements) {
        return this.queue.drainTo(c, maxElements);
    }

}
