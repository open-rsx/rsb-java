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
package rsb.transport.socket;

/**
 * Class with helpers and constants describing the socket-based protocol.
 *
 * @author jwienke
 */
public final class Protocol {

    /**
     * The data to send for handshake requests and replies.
     */
    public static final int HANDSHAKE_DATA = 0x00000000;

    /**
     * The number of bytes to send and expect for handshake messages.
     */
    public static final int HANDSHAKE_BYTES = 4;

    /**
     * The number of bytes encoding the data size to be sent.
     */
    public static final int DATA_SIZE_BYTES = 4;

    private Protocol() {
        super();
        // prevent instantiation of utility class
    }

}
