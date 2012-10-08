/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010, 2011, 2012 CoR-Lab, Bielefeld University
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

// mark-start::body
import java.lang.System;
import java.lang.Thread;

import rsb.AbstractEventHandler;
import rsb.Factory;
import rsb.Event;
import rsb.Listener;

public class EventListenerExample extends AbstractEventHandler {

    @Override
    public void handleEvent(Event event) {
        System.out.println("Received event " + event.toString());
    }

    public static void main(String[] args) throws Throwable {
        // Get a factory instance to create new RSB objects.
        Factory factory = Factory.getInstance();

        // Create a Listener instance on the specified scope that will
        // receive events and dispatch them asynchronously to all
        // registered handlers; activate the listener.
        Listener listener = factory.createListener("/example/informer");
        listener.activate();

        try {
            // Add an EventHandler that will print events when they
            // are received.
            listener.addHandler(new EventListenerExample(), true);

            // Wait for events.
            while (true) {
                Thread.sleep(1);
            }
        } finally {
            // Deactivate the listener after use.
            listener.deactivate();
        }
    }

}
// mark-end::body
