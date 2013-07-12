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
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;
import rsb.transport.TransportFactory;

/**
 * @author jwienke
 * @author swrede
 */
public class SpreadFactory implements TransportFactory {

    @Override
    public InPushConnector createInPushConnector() {
        final ConverterSelectionStrategy<ByteBuffer> inStrategy = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForDeserialization();
        final InPushConnector connector = new SpreadInPushConnector(
                new SpreadWrapper(), inStrategy);
        return connector;
    }

    @Override
    public OutConnector createOutConnector() {
        final ConverterSelectionStrategy<ByteBuffer> outStrategy = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForSerialization();
        return new SpreadOutConnector(new SpreadWrapper(), outStrategy);
    }
}
