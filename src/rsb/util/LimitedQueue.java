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
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals",
        "PMD.UseVarargs" })
public class LimitedQueue<T> implements BlockingQueue<T> {

    protected BlockingQueue<T> queue;

    public LimitedQueue(final int capacity) {
        this.queue = new LinkedBlockingQueue<T>(capacity);
    }

    @Override
    public T remove() {
        synchronized (this) {
            return this.queue.remove();
        }
    }

    @Override
    public T poll() {
        synchronized (this) {
            return this.queue.poll();
        }
    }

    @Override
    public T element() {
        synchronized (this) {
            return this.queue.element();
        }
    }

    @Override
    public T peek() {
        synchronized (this) {
            return this.queue.peek();
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            return this.queue.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (this) {
            return this.queue.isEmpty();
        }
    }

    @Override
    public Iterator<T> iterator() {
        synchronized (this) {
            return this.queue.iterator();
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (this) {
            return this.queue.toArray();
        }
    }

    @Override
    public <U> U[] toArray(@SuppressWarnings("PMD.ShortVariable") final U[] a) {
        synchronized (this) {
            return this.queue.toArray(a);
        }
    }

    @Override
    public boolean containsAll(
            @SuppressWarnings("PMD.ShortVariable") final Collection<?> c) {
        synchronized (this) {
            return this.queue.containsAll(c);
        }
    }

    @Override
    public boolean addAll(
            @SuppressWarnings("PMD.ShortVariable") final Collection<? extends T> c) {
        synchronized (this) {
            return this.queue.addAll(c);
        }
    }

    @Override
    public boolean removeAll(
            @SuppressWarnings("PMD.ShortVariable") final Collection<?> c) {
        synchronized (this) {
            return this.queue.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(
            @SuppressWarnings("PMD.ShortVariable") final Collection<?> c) {
        synchronized (this) {
            return this.queue.retainAll(c);
        }
    }

    @Override
    public void clear() {
        synchronized (this) {
            this.queue.clear();
        }

    }

    @Override
    public boolean add(@SuppressWarnings("PMD.ShortVariable") final T e) {
        if (this.queue.remainingCapacity() == 0) {
            this.queue.poll();
        }
        return this.queue.add(e);
    }

    @Override
    public boolean offer(@SuppressWarnings("PMD.ShortVariable") final T e) {
        return this.queue.offer(e);
    }

    @Override
    public void put(@SuppressWarnings("PMD.ShortVariable") final T e)
            throws InterruptedException {
        this.queue.put(e);

    }

    @Override
    public boolean offer(@SuppressWarnings("PMD.ShortVariable") final T e,
            final long timeout, final TimeUnit unit)
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
    public boolean remove(@SuppressWarnings("PMD.ShortVariable") final Object o) {
        return this.queue.remove(o);
    }

    @Override
    public boolean contains(
            @SuppressWarnings("PMD.ShortVariable") final Object o) {
        return this.queue.contains(o);
    }

    @Override
    public int drainTo(
            @SuppressWarnings("PMD.ShortVariable") final Collection<? super T> c) {
        return this.queue.drainTo(c);
    }

    @Override
    public int drainTo(
            @SuppressWarnings("PMD.ShortVariable") final Collection<? super T> c,
            final int maxElements) {
        return this.queue.drainTo(c, maxElements);
    }

}
