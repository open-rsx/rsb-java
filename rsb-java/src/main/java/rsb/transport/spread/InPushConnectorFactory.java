/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2015 CoR-Lab, Bielefeld University
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

import rsb.InitializeException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.transport.InPushConnector;
import rsb.util.Properties;

/**
 * Interface for strategies used to create {@link SpreadInPushConnector}
 * instances depending on the client's needs.
 *
 * @author jwienke
 */
public interface InPushConnectorFactory {

    /**
     * Creates a new instance of a {@link SpreadInPushConnector} based on the
     * provided options.
     *
     * @param properties
     *            the specific transport configuration, not <code>null</code>
     * @param converters
     *            the converters to use for the transport
     * @return an appropriate connector instance for the provided options, not
     *         <code>null</code>
     * @throws InitializeException
     *             unable to initialize a connector for the provided options
     */
    InPushConnector create(final Properties properties,
            final ConverterSelectionStrategy<?> converters)
            throws InitializeException;
}
