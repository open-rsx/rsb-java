/*
 * Copyright 2010,2011 Bielefeld University
 * Copyright 2011 David Klotz <david -at- sofaecke -dot- org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rsb.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import rsb.AbstractEventHandler;
import rsb.Event;
import rsb.util.QueueAdapter;

/**
 * An adapter similar to the {@link QueueAdapter} that provides access to a
 * queue of RSB {@link Event}s instead of directly to the payload data. It can
 * also be directly registered as handler in an rsb.Listener instance and used
 * for receiving and storing dispatched events.
 * 
 * @author dklotz
 */
public class EventQueueAdapter extends AbstractEventHandler {
	BlockingQueue<Event> queue;

	/**
	 * Creates an adapter with a preset unlimited queue inside.
	 */
	public EventQueueAdapter() {
		queue = new LinkedBlockingDeque<Event>();
	}

	/**
	 * Creates an adapter with a preset queue inside that is limited to
	 * <code>capacity</code> elements.
	 * 
	 * @param capacity
	 *            capacity of the internal queue
	 */
	public EventQueueAdapter(final int capacity, final boolean discardOldest) {
		if (!discardOldest) {
			queue = new LinkedBlockingDeque<Event>(capacity);
		} else {
			queue = new LimitedQueue<Event>(capacity);
		}
	}

	/**
	 * Creates an adapter with the given queue implementation.
	 * 
	 * @param queue
	 *            The queue this adapter should fill.
	 */
	public EventQueueAdapter(BlockingQueue<Event> queue) {
		this.queue = queue;
	}

	@Override
	public void handleEvent(Event event) {
		this.queue.add(event);
	}

	/**
	 * Provides access to the queue filled by this queue adapter.
	 * 
	 * @return The current with the events received by this adapter.
	 */
	public BlockingQueue<Event> getQueue() {
		return queue;
	}
}
