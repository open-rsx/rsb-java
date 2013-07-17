/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011, 2012 CoR-Lab, Bielefeld University
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
import rsb.Event;
import rsb.Factory;
import rsb.patterns.EventCallback;
import rsb.patterns.LocalServer;

public class ServerExample {

    public static class EchoCallback extends EventCallback {

        @Override
        public Event invoke(final Event request) throws Throwable {
            return new Event(String.class, request.getData());
        }

    }

    public static void main(final String[] args) throws Throwable {
        // Get local server object which allows to expose remotely
        // callable methods.
        final LocalServer server = Factory.getInstance().createLocalServer(
                "/example/server");
        server.activate();

        // Add method an "echo" method, implemented by EchoCallback.
        server.addMethod("echo", new EchoCallback());

        // Block until server.deactivate or process shutdown
        server.waitForShutdown();
    }

}
// mark-end::body
