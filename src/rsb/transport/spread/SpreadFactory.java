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

import java.nio.ByteBuffer;

import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.DefaultConverterRepository;
import rsb.transport.EventHandler;
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;
import rsb.transport.TransportFactory;

/**
 * 
 * @author swrede
 */
public class SpreadFactory extends TransportFactory {

    @Override
    public InPushConnector createInConnector(final EventHandler handler) {
        final InPushConnector connector = createConnector();
        connector.addHandler(handler);
        return connector;
    }

    private SpreadPort createConnector() {
        final ConverterSelectionStrategy<ByteBuffer> inStrategy = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForDeserialization();
        // inStrategy.addConverter("utf-8-string", new StringConverter());
        // inStrategy.addConverter("ascii-string", new
        // StringConverter("US-ASCII", "ascii-string"));

        final ConverterSelectionStrategy<ByteBuffer> outStrategy = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForSerialization();
        // outStrategy.addConverter("String", new StringConverter());

        return new SpreadPort(new SpreadWrapper(), inStrategy, outStrategy);
    }

    @Override
    public OutConnector createOutConnector() {
        return createConnector();
    }

}
