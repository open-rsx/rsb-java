/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2014 CoR-Lab, Bielefeld University
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
package rsb.util.os;


/**
 * Interface for host information model classes.
 *
 * @author swrede
 */
public interface HostInfo {

    /**
     * Returns a unique id describing a host. This ID should be as persistent as
     * possible.
     *
     * @return string representation of the ID, might be <code>null</code> in
     *         case it could not be determined
     */
    String getHostId();

    /**
     * Returns a human-readable host name that might change during runtime of
     * the host.
     *
     * @return string, might be <code>null</code> in case the host name could
     *         not be determined
     */
    String getHostName();

    /**
     * Returns the name of the underlying software host system (OS).
     *
     * @return the name, might be <code>null</code> in case it could not be
     *         determined
     */
    String getSoftwareType();

    /**
     * Returns the computer architecture of this host.
     *
     * @return system architecture, might be <code>null</code> in case
     *         it could not be determined
     */
    String getMachineType();

}
