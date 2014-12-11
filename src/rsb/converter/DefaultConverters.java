/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011, 2013 CoR-Lab, Bielefeld University
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

package rsb.converter;

import rsb.protocol.introspection.ByeType.Bye;
import rsb.protocol.introspection.HelloType.Hello;
import rsb.protocol.operatingsystem.HostType.Host;
import rsb.protocol.operatingsystem.ProcessType.Process;

/**
 * @author swrede
 */
public final class DefaultConverters {

    private DefaultConverters() {
        // make this a utility class which cannot be instantiated
        super();
    }

    /**
     * Convenience method to register default converters for default wire type.
     */
    public static void register() {
        // TODO add missing converters for default types
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new StringConverter());
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new StringConverter("US-ASCII", "ascii-string"));
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new BoolConverter());
        // DefaultConverterRepository.getDefaultConverterRepository()
        // .addConverter(new Uint64Converter());
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new EventIdConverter());
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new NullConverter());
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new Int64Converter());
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new DoubleConverter());

        // Converter instances required for introspection subsystem
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new ProtocolBufferConverter<Hello>(Hello.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new ProtocolBufferConverter<Bye>(Bye.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new ProtocolBufferConverter<Host>(Host.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository()
                .addConverter(new ProtocolBufferConverter<Process>(Process.getDefaultInstance()));
    }

}
