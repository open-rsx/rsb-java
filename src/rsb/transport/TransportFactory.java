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

import rsb.InitializeException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.util.Properties;

/**
 * Interface for factories providing implementations of {@link Connector}
 * instances for a certain transport.
 *
 * @author jwienke
 * @author swrede
 */
public interface TransportFactory {

    /**
     * Creates an out connector for sending events.
     *
     * @param properties
     *            specific options for the connector to be created
     * @param converters
     *            the converter selection strategy to use in the new connector
     * @return new connector instance
     * @throws InitializeException
     *             error creating a new connector
     */
    OutConnector createOutConnector(Properties properties,
            ConverterSelectionStrategy<?> converters)
            throws InitializeException;

    /**
     * Creates an in connector for receiving events in an asynchronous fashion.
     *
     * @param properties
     *            specific options for the connector to be created
     * @param converters
     *            the converter selection strategy to use in the new connector
     * @return new connector instance
     * @throws InitializeException
     *             error creating a new connector
     */
    InPushConnector createInPushConnector(Properties properties,
            ConverterSelectionStrategy<?> converters)
            throws InitializeException;

}
