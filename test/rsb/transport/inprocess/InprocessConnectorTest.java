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
package rsb.transport.inprocess;

import org.junit.Test;

import rsb.transport.ConnectorCheck;
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;

/**
 * Test for in process connectors.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class InprocessConnectorTest extends ConnectorCheck {

    private final rsb.transport.inprocess.Bus bus =
            new rsb.transport.inprocess.Bus();

    @Override
    protected InPushConnector createInConnector() throws Throwable {
        return new rsb.transport.inprocess.InPushConnector(this.bus);
    }

    @Override
    protected OutConnector createOutConnector() throws Throwable {
        return new rsb.transport.inprocess.OutConnector(this.bus);
    }

    @Test
    @Override
    public void noConverterPassedToClient() throws Throwable {
        // not applicable for inprocess
    }

}
