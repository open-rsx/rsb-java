/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultConverterRepository<WireType> implements
        ConverterRepository<WireType> {

    private static final Logger LOG = Logger
            .getLogger(DefaultConverterRepository.class.getName());

    private static ConverterRepository<ByteBuffer> defaultInstance =
            new DefaultConverterRepository<ByteBuffer>();

    private final Map<ConverterSignature, Converter<WireType>> converterMap =
            new HashMap<ConverterSignature, Converter<WireType>>();

    /**
     * @return the converterMap
     */
    protected Map<ConverterSignature, Converter<WireType>> getConverterMap() {
        return this.converterMap;
    }

    @Override
    public ConverterSelectionStrategy<WireType> getConvertersForSerialization() {
        final UnambiguousConverterMap<WireType> outStrategy =
                new UnambiguousConverterMap<WireType>();
        // where to register the initial converters
        // outStrategy.addConverter("String", new StringConverter());
        // Query Map for types
        for (final ConverterSignature signature : this.converterMap.keySet()) {
            // put datatype and converter into unambiguous converter map
            if (signature.getSchema().contentEquals("ascii-string")) {
                // adding two String representations would yield ambiguity
                // we want to use the UTF-8 representation for strings as
                // default
                LOG.fine("skipping ascii-string converter for Serialization map");
            } else {
                // all other converters are added at this point
                outStrategy.addConverter(signature.getDataType().getName(),
                        this.converterMap.get(signature));
            }
        }
        return outStrategy;
    }

    @Override
    public ConverterSelectionStrategy<WireType> getConvertersForDeserialization() {
        final UnambiguousConverterMap<WireType> inStrategy =
                new UnambiguousConverterMap<WireType>();
        // Query Map for wire schemas
        for (final ConverterSignature signature : this.converterMap.keySet()) {
            // put datatype and converter into unambiguous converter map
            inStrategy.addConverter(signature.getSchema(),
                    this.converterMap.get(signature));
        }
        return inStrategy;
    }

    @Override
    public void addConverter(final Converter<WireType> converter) {
        if (this.converterMap.containsKey(converter.getSignature())) {
            LOG.log(Level.WARNING,
                    "Converter with signature {} already registered in "
                            + "DefaultConverterRepository. "
                            + "Existing entry will be overwritten!",
                    converter.getSignature());
        }
        this.converterMap.put(converter.getSignature(), converter);
    }

    /**
     * Returns a global repository for wire type {@link ByteBuffer}.
     *
     * @return repository instance
     */
    public static ConverterRepository<ByteBuffer> getDefaultConverterRepository() {
        return defaultInstance;
    }

}
