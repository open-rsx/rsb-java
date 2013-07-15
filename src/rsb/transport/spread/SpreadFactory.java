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

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import rsb.InitializeException;
import rsb.QualityOfServiceSpec;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.DefaultConverterRepository;
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;
import rsb.transport.TransportFactory;
import rsb.util.Properties;

/**
 * @author jwienke
 * @author swrede
 */
public class SpreadFactory implements TransportFactory {

    private SpreadWrapper createSpreadWrapper(final Properties properties)
            throws InitializeException {
        try {
            return new SpreadWrapper(properties.getProperty(
                    "transport.spread.host", "localhost").asString(),
                    properties.getProperty("transport.spread.port", "4803")
                            .asInteger(), properties.getProperty(
                            "transport.spread.tcpnodelay", "true").asBoolean());
        } catch (final NumberFormatException e) {
            throw new InitializeException(
                    "Unable to parse spread port from properties.", e);
        } catch (final UnknownHostException e) {
            throw new InitializeException("Unable to resolve host name "
                    + properties.getProperty("transport.spread.host",
                            "localhost"), e);
        }
    }

    @Override
    public InPushConnector createInPushConnector(final Properties properties)
            throws InitializeException {
        final ConverterSelectionStrategy<ByteBuffer> inStrategy = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForDeserialization();
        return new SpreadInPushConnector(createSpreadWrapper(properties),
                inStrategy);

    }

    @Override
    public OutConnector createOutConnector(final Properties properties)
            throws InitializeException {

        final ConverterSelectionStrategy<ByteBuffer> outStrategy = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForSerialization();

        Ordering ordering = new QualityOfServiceSpec().getOrdering();
        if (properties.hasProperty("qualityofservice.ordering")) {
            ordering = Ordering.valueOf(properties.getProperty(
                    "qualityofservice.ordering").asString());
        }
        Reliability reliability = new QualityOfServiceSpec().getReliability();
        if (properties.hasProperty("qualityofservice.reliability")) {
            reliability = Reliability.valueOf(properties.getProperty(
                    "qualityofservice.reliability").asString());
        }

        return new SpreadOutConnector(createSpreadWrapper(properties),
                outStrategy, new QualityOfServiceSpec(ordering, reliability));

    }
}
