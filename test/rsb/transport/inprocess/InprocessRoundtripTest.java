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

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rsb.transport.ConnectorRoundtripCheck;
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;

/**
 * Roundtrip test for the inprocess transport on user level.
 *
 * @author jwienke
 */
@RunWith(value = Parameterized.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class InprocessRoundtripTest extends ConnectorRoundtripCheck {

    private final Bus bus = new Bus();

    public InprocessRoundtripTest(final int size) {
        super(size);
    }

    @Parameters
    public static Collection<Object[]> data() {
        final Object[][] data = new Object[][] { { 100 } };
        return Arrays.asList(data);
    }

    @Override
    protected OutConnector createOutConnector() throws Throwable {
        return new rsb.transport.inprocess.OutConnector(this.bus);
    }

    @Override
    protected InPushConnector createInConnector() throws Throwable {
        return new rsb.transport.inprocess.InPushConnector(this.bus);
    }

}
