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
import rsb.Factory;
import rsb.patterns.RemoteServer;

public class ClientExample {

    public static void main(final String[] args) throws Throwable {
        // Get remote server object to call exposed request methods of
        // participants
        final RemoteServer server = Factory.getInstance().createRemoteServer(
                "/example/server");
        server.activate();

        // Call remote method and deactivate the server.
        try {

            System.out.println("Server replied: " + server.call("echo", "bla"));
        } finally {
            server.deactivate();
        }
    }

}
// mark-end::body
