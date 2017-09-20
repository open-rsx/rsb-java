/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2017 CoR-Lab, Bielefeld University
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
package rsb;

import java.io.IOException;

/**
 * Implements close method.
 *
 * @author jmoringe
 */
public abstract class AbstractActivatable implements Activatable {

    @Override
    public void close() throws IOException {
        try {
            if (isActive()) {
                deactivate();
            }
        } catch (RSBException exception) {
            throw new IOException("Failed to deactivate participant " + this,
                                  exception);
        } catch (InterruptedException exception) {
            // We are strongly urged to not throw an
            // InterruptedException in this case. So we just disregard
            // which is apparently better than potentially disrupting
            // the control flow of the surrounding program.
            // See https://docs.oracle.com/javase/7/
            // docs/api/java/lang/AutoCloseable.html#close()
        }
    }

}
