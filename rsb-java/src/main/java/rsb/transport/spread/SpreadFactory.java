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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.QualityOfServiceSpec;
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

    private static final String SCHEMA = "spread";

    private static final Logger LOG = Logger.getLogger(SpreadFactory.class
            .getName());

    private static final InPushConnectorFactory DEFAULT_IN_FACTORY =
            new IndividualInPushConnectorFactory();

    private static final String IN_FACTORY_KEY =
            "transport.spread.java.infactory";

    private final Map<SpreadOptions, SpreadWrapper> spreadWrappers =
            new HashMap<SpreadOptions, SpreadWrapper>();

    private SpreadWrapper createSpreadWrapper(final SpreadOptions options)
            throws InitializeException {
        LOG.log(Level.FINE, "Creating a spread wrapper with options {0}",
                new Object[] { options });
        try {
            return new SpreadWrapperImpl(options);
        } catch (final NumberFormatException e) {
            throw new InitializeException(
                    "Unable to parse spread port from properties.", e);
        } catch (final UnknownHostException e) {
            throw new InitializeException("Unable to resolve host name "
                    + options.getHost(), e);
        }
    }

    private SpreadWrapper
            createUniqueSpreadWrapper(final SpreadOptions options)
                    throws InitializeException {
        synchronized (this.spreadWrappers) {
            // this always leaves the SpreadWrapper instances in the map since
            // we know that reactivation is possible for these instances
            if (!this.spreadWrappers.containsKey(options)) {
                this.spreadWrappers.put(options, new RefCountingSpreadWrapper(
                        createSpreadWrapper(options)));
            }
            return this.spreadWrappers.get(options);
        }
    }

    @Override
    public ConnectorInfo getInfo() {
        final Set<String> schemas = new HashSet<String>();
        schemas.add(SCHEMA);
        final Set<String> options = new HashSet<String>();
        options.add("host");
        options.add("port");
        options.add("tcpnodelay");
        return new ConnectorInfo(SCHEMA, schemas, options, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InPushConnector createInPushConnector(final Properties properties,
            final ConverterSelectionStrategy<?> converters)
            throws InitializeException {

        InPushConnectorFactory factory = DEFAULT_IN_FACTORY;
        if (properties.hasProperty(IN_FACTORY_KEY)) {
            factory =
                    InPushConnectorFactoryRegistry.getInstance().get(
                            properties.getProperty(IN_FACTORY_KEY).asString());
            if (factory == null) {
                throw new InitializeException(
                        "InPushConnectorFactory with key '"
                                + properties.getProperty(IN_FACTORY_KEY)
                                        .asString()
                                + "' not available in the registry");
            }
        }

        LOG.log(Level.FINE,
                "Creating an in-push connector from properties {0} "
                        + "with factory {1} and converters {2}", new Object[] {
                        properties, factory, converters });

        return factory.create(properties, converters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public OutConnector createOutConnector(final Properties properties,
            final ConverterSelectionStrategy<?> converters)
            throws InitializeException {
        LOG.log(Level.FINE,
                "Creating and out connector with properties {0} and converters {1}",
                new Object[] { properties, converters });

        final QualityOfServiceSpec qos =
                ConfigParseUtilities.parseQualityOfServiceSpec(properties);
        return new SpreadOutConnector(
                createUniqueSpreadWrapper(ConfigParseUtilities
                        .spreadOptionsFromProperties(properties)),
                (ConverterSelectionStrategy<ByteBuffer>) converters, qos);

    }
}
