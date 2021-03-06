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
package rsb.transport.socket;

import java.nio.ByteBuffer;

import org.junit.Rule;

import rsb.converter.UnambiguousConverterMap;
import rsb.testutils.ConnectorCheck;
import rsb.testutils.ParticipantConfigSetter;
import rsb.transport.InConnector;
import rsb.transport.OutConnector;

/**
 * Test for socket connectors.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class SocketConnectorTest extends ConnectorCheck {

    // CHECKSTYLE.OFF: VisibilityModifier - required by junit
    /**
     * Rule to set a default participant config.
     */
    @Rule
    public final ParticipantConfigSetter setter = new ParticipantConfigSetter(
            rsb.Utilities.createParticipantConfig());
    // CHECKSTYLE.ON: VisibilityModifier

    @Override
    protected InConnector createInConnector(
            final UnambiguousConverterMap<ByteBuffer> converters)
            throws Throwable {
        return new SocketInConnector(
                rsb.transport.socket.Utilities.getSocketOptions(),
                ServerMode.AUTO, converters);
    }

    @Override
    protected OutConnector createOutConnector(
            final UnambiguousConverterMap<ByteBuffer> converters)
            throws Throwable {
        return new SocketOutConnector(Utilities.getSocketOptions(),
                ServerMode.AUTO, converters);
    }

}
