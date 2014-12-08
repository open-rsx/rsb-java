/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010,2011 CoR-Lab, Bielefeld University
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
package rsb.util;

/**
 * Utility class to determine type of operating system.
 *
 * @author swrede
 */
public class OSDetector {

    public static OSFamily getOSFamily() {
        final String identifier = System.getProperty("os.name");
        if (identifier.startsWith("Windows")) {
            return OSFamily.WIN32;
        } else if (identifier.startsWith("Linux")) {
            return OSFamily.LINUX;
        } else if (identifier.startsWith("Mac")) {
            return OSFamily.DARWIN;
        } else {
            return OSFamily.UNKNOWN;
        }
    }

}
