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
package rsb.apps;

import rsb.Version;
import rsb.util.ExactTime;

/**
 * A simply executable to dump the RSB version to the command line.
 *
 * @author jwienke
 */
public final class DumpVersion {

    private DumpVersion() {
        // empty constructor to prevent initialization
    }

    /**
     * Main method.
     *
     * @param args
     *            command line args
     * @throws Throwable
     *             in case of any error
     */
    @SuppressWarnings({ "PMD.UseVarargs", "PMD.SystemPrintln" })
    public static void main(final String[] args) throws Throwable {
        System.out.println(
                "RSB Version: " + Version.getInstance().getVersionString());
        System.out.println("Current time: " + ExactTime.currentTimeMicros()
                + " (fallback implementation: " + ExactTime.isFallback() + ")");
    }
}
