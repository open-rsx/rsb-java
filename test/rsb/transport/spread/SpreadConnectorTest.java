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
package rsb.transport.spread;

import rsb.QualityOfServiceSpec;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.Utilities;
import rsb.transport.ConnectorCheck;
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;

/**
 * Test for spread connectors.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class SpreadConnectorTest extends ConnectorCheck {

    @Override
    protected InPushConnector createInConnector() throws Throwable {
        final SpreadWrapper inWrapper = Utilities.createSpreadWrapper();
        final SpreadInPushConnector inPort = new SpreadInPushConnector(
                inWrapper, this.getConverterStrategy("utf-8-string"));
        return inPort;
    }

    @Override
    protected OutConnector createOutConnector() throws Throwable {
        final SpreadWrapper outWrapper = Utilities.createSpreadWrapper();
        final SpreadOutConnector outConnector = new SpreadOutConnector(
                outWrapper,
                this.getConverterStrategy(String.class.getName()),
                new QualityOfServiceSpec(Ordering.ORDERED, Reliability.RELIABLE));
        return outConnector;
    }

}
