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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import rsb.AbstractDataHandler;

/**
 * Synchronized queue implementing the rsb.DataHandler interface.
 * Can be directly registered as handler in rsb.Listener instance
 * and used for receiving and storing dispatched events.
 * 
 * @author swrede
 * @author dklotz
 */
public class QueueAdapter<T> extends AbstractDataHandler<T> { 
        BlockingQueue<T> queue;
        
        public QueueAdapter() {
            queue = new LinkedBlockingDeque<T>();
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
