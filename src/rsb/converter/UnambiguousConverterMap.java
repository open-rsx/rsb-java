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

import java.util.HashMap;
import java.util.Map;

/**
 * @author swrede
 * @author jmoringe
 * @param <WireType>
 */
public class UnambiguousConverterMap<WireType> implements
        ConverterSelectionStrategy<WireType> {

    private final Map<String, Converter<WireType>> converters =
            new HashMap<String, Converter<WireType>>();

    @Override
    public Converter<WireType> getConverter(final String key) {
        if (this.converters.containsKey(key)) {
            return this.converters.get(key);
        }
        throw new NoSuchConverterException("No converter with key " + key
                + " registered in ConverterMap");
    }

    /**
     * Adds a new converter to this map which will be registered under a given
     * key.
     *
     * @param key
     *            the key
     * @param converter
     *            the converter
     * @throws IllegalArgumentException
     *             in case there already is a converter with the specified key
     */
    public void addConverter(final String key,
            final Converter<WireType> converter) {
        if (this.converters.containsKey(key)) {
            throw new IllegalArgumentException(
                    "There is already a converter with key '" + key + "'.");
        }
        this.converters.put(key, converter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime
                        * result
                        + ((this.converters == null) ? 0 : this.converters
                                .hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UnambiguousConverterMap)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        final UnambiguousConverterMap other = (UnambiguousConverterMap) obj;
        if (this.converters == null) {
            if (other.converters != null) {
                return false;
            }
        } else if (!this.converters.equals(other.converters)) {
            return false;
        }
        return true;
    }

}
