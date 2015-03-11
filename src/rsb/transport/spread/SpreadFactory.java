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
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;
import rsb.transport.TransportFactory;
import rsb.util.Properties;

/**
 * A {@link TransportFactory} implementation for the spread transport.
 *
 * @author jwienke
 * @author swrede
 */
public class SpreadFactory implements TransportFactory {

    private static final String QOS_RELIABILITY_KEY =
            "qualityofservice.reliability";
    private static final String QOS_ORDERING_KEY = "qualityofservice.ordering";
    private static final String HOST_KEY = "transport.spread.host";
    private static final String DEFAULT_HOST = "localhost";
    private static final String PORT_KEY = "transport.spread.port";
    private static final String DEFAULT_PORT = "4803";
    private static final String NODELAY_KEY = "transport.spread.tcpnodelay";
    private static final String DEFAULT_NODELAY = "true";

    private SpreadOptions optionsFromProperties(final Properties properties) {
        return new SpreadOptions(properties.getProperty(HOST_KEY, DEFAULT_HOST)
                .asString(), properties.getProperty(PORT_KEY, DEFAULT_PORT)
                .asInteger(), properties.getProperty(NODELAY_KEY,
                DEFAULT_NODELAY).asBoolean());
    }

    private SpreadWrapper createSpreadWrapper(final SpreadOptions options)
            throws InitializeException {
        try {
            return new SpreadWrapper(options);
        } catch (final NumberFormatException e) {
            throw new InitializeException(
                    "Unable to parse spread port from properties.", e);
        } catch (final UnknownHostException e) {
            throw new InitializeException("Unable to resolve host name "
                    + options.getHost(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public InPushConnector createInPushConnector(final Properties properties,
            final ConverterSelectionStrategy<?> converters)
            throws InitializeException {
        return new SpreadInPushConnector(
                createSpreadWrapper(optionsFromProperties(properties)),
                (ConverterSelectionStrategy<ByteBuffer>) converters);

    }

    @SuppressWarnings("unchecked")
    @Override
    public OutConnector createOutConnector(final Properties properties,
            final ConverterSelectionStrategy<?> converters)
            throws InitializeException {

        Ordering ordering = new QualityOfServiceSpec().getOrdering();
        if (properties.hasProperty(QOS_ORDERING_KEY)) {
            ordering =
                    Ordering.valueOf(properties.getProperty(QOS_ORDERING_KEY)
                            .asString());
        }
        Reliability reliability = new QualityOfServiceSpec().getReliability();
        if (properties.hasProperty(QOS_RELIABILITY_KEY)) {
            reliability =
                    Reliability.valueOf(properties.getProperty(
                            QOS_RELIABILITY_KEY).asString());
        }

        return new SpreadOutConnector(
                createSpreadWrapper(optionsFromProperties(properties)),
                (ConverterSelectionStrategy<ByteBuffer>) converters,
                new QualityOfServiceSpec(ordering, reliability));

    }
}
