/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
 * @param <ElementType>
 *            contained element type
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals",
        "PMD.UseVarargs" })
public class LimitedQueue<ElementType> implements BlockingQueue<ElementType> {

    private final BlockingQueue<ElementType> queue;

    /**
     * Creates a queue with the specified capacity. Oldest elements are removed
     * on add in case the capacity limit is reached.
     *
     * @param capacity
     *            capacity of the queue
     */
    public LimitedQueue(final int capacity) {
        this.queue = new LinkedBlockingQueue<ElementType>(capacity);
    }

    /**
     * Returns the underlying queue.
     *
     * @return queue instance
     */
    protected BlockingQueue<ElementType> getQueue() {
        return this.queue;
    }

    @Override
    public ElementType remove() {
        synchronized (this) {
            return this.queue.remove();
        }
    }

    @Override
    public ElementType poll() {
        synchronized (this) {
            return this.queue.poll();
        }
    }

    @Override
    public ElementType element() {
        synchronized (this) {
            return this.queue.element();
        }
    }

    @Override
    public ElementType peek() {
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
    public Iterator<ElementType> iterator() {
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
    public <TargetType> TargetType[] toArray(
            @SuppressWarnings("PMD.ShortVariable") final TargetType[] a) {
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
            @SuppressWarnings("PMD.ShortVariable")
            final Collection<? extends ElementType> c) {
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
    public boolean add(
            @SuppressWarnings("PMD.ShortVariable") final ElementType e) {
        if (this.queue.remainingCapacity() == 0) {
            this.queue.poll();
        }
        return this.queue.add(e);
    }

    @Override
    public boolean offer(
            @SuppressWarnings("PMD.ShortVariable") final ElementType e) {
        return this.queue.offer(e);
    }

    @Override
    public void put(@SuppressWarnings("PMD.ShortVariable") final ElementType e)
            throws InterruptedException {
        this.queue.put(e);

    }

    @Override
    public boolean offer(
            @SuppressWarnings("PMD.ShortVariable") final ElementType e,
            final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return this.queue.offer(e, timeout, unit);
    }

    @Override
    public ElementType take() throws InterruptedException {
        return this.queue.take();
    }

    @Override
    public ElementType poll(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return this.queue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return this.queue.remainingCapacity();
    }

    @Override
    public boolean
            remove(@SuppressWarnings("PMD.ShortVariable") final Object o) {
        return this.queue.remove(o);
    }

    @Override
    public boolean contains(
            @SuppressWarnings("PMD.ShortVariable") final Object o) {
        return this.queue.contains(o);
    }

    // CHECKSTYLE.OFF: LineLength - Eclipse will not format this better
    @Override
    public
            int
            drainTo(@SuppressWarnings("PMD.ShortVariable") final Collection<? super ElementType> c) {
        return this.queue.drainTo(c);
    }

    @Override
    public
            int
            drainTo(@SuppressWarnings("PMD.ShortVariable") final Collection<? super ElementType> c,
                    final int maxElements) {
        return this.queue.drainTo(c, maxElements);
    }
    // CHECKSTYLE.ON: LineLength

}
