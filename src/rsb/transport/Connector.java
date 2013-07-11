/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
package rsb.transport;

import rsb.QualityOfServiceSpec;
import rsb.RSBObject;

/**
 * A generic interface for classes providing connection to a specific transport
 * mechanism.
 * 
 * @author jwienke
 * @author swrede
 */
public interface Connector extends RSBObject {

    // TODO still required? What was the original meaning
    String getType();

    /**
     * Sets the quality of service requirements on sending and receiving event
     * notifications.
     * 
     * @param spec
     *            new spec to apply
     */
    void setQualityOfServiceSpec(QualityOfServiceSpec spec);

}
