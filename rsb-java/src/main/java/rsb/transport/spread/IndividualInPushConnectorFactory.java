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

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import rsb.InitializeException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.transport.InPushConnector;
import rsb.util.Properties;

/**
 * A {@link InPushConnectorFactory} implementation assigning an individual
 * connector to each creation request.
 *
 * @author jwienke
 */
public class IndividualInPushConnectorFactory implements InPushConnectorFactory {

    // TODO remove duplication
    private SpreadWrapper createSpreadWrapper(final SpreadOptions options)
            throws InitializeException {
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

    @SuppressWarnings("unchecked")
    @Override
    public InPushConnector create(final Properties properties,
            final ConverterSelectionStrategy<?> converters)
            throws InitializeException {
        return new SpreadInPushConnector(
                createSpreadWrapper(ConfigParseUtilities
                        .spreadOptionsFromProperties(properties)),
                (ConverterSelectionStrategy<ByteBuffer>) converters);
    }

}
