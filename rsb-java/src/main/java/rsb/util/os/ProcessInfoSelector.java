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
 * Utility class to select a {@link ProcessInfo} instance for the operating
 * system a process is currently being executed on.
 *
 * @author jwienke
 */
public final class ProcessInfoSelector {

    private ProcessInfoSelector() {
        // prevent instantiation of utility class
    }

    /**
     * Returns the "best" matching {@link ProcessInfo} implementation for the
     * current operating system.
     *
     * @return process info instance, not <code>null</code>
     */
    // for later extension with other OSes
    @SuppressWarnings("PMD.TooFewBranchesForASwitchStatement")
    public static ProcessInfo getProcessInfo() {
        switch (OsUtilities.deriveOsFamily(OsUtilities.getOsName())) {
        case LINUX:
            return new LinuxProcessInfo();
        default:
            return new PortableProcessInfo();
        }
    }

}
