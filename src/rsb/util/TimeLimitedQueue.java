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

import java.util.concurrent.TimeUnit;

import rsb.Event;

/**
 * A time and space limited queue of RSB {@link Event}s that discards past
 * events older than a specified time limit. It will additionally discard old
 * events when reaching a set maximum capacity.
 * 
 * @author dklotz
 */
public class TimeLimitedQueue extends LimitedQueue<Event> {

	private long timeWindow;

	public TimeLimitedQueue(int capacity, long timeWindow, TimeUnit unit) {
		super(capacity);
		this.timeWindow = TimeUnit.MICROSECONDS.convert(timeWindow, unit);
	}
	
	private synchronized void discardOldEvents(long currentTime) {
		if (queue.isEmpty()) {
			return;
		}
		
		Event oldestEvent = queue.peek();
		long oldestTime = oldestEvent.getMetaData().getCreateTime();
		
		while (((currentTime - oldestTime) > timeWindow) && !queue.isEmpty()) {
			// Discard the oldest element
			queue.poll();
			
			// Get the age of the next oldest element
			oldestEvent = queue.peek();
			oldestTime = oldestEvent.getMetaData().getCreateTime();
		}
	}

	@Override
	public boolean add(Event e) {
		// Discard events outside the time window.
		// TODO: Do we always want to use the create time?
		long currentTime = e.getMetaData().getCreateTime();
		discardOldEvents(currentTime);
		
		return super.add(e);
	}

	// TODO: Shouldn't the "offer" methods also be overwritten in such a queue?
}
