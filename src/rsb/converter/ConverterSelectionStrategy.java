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

/**
 * Implementation of this interface perform mappings of one of the followings
 * forms: - wire-schema -> @ref Converter - data-type -> @ref Converter
 * 
 * @author jmoringe
 * @author swrede
 * @param <WireType>
 *            the kind of wires contained converters can deal with
 */
public interface ConverterSelectionStrategy<WireType> {

    /**
     * Tries to look up the converter designated by @a key.
     * 
     * @param key
     * @return the converter for the given key
     * @throws NoSuchConverterException
     *             no converter found for that key
     */
    Converter<WireType> getConverter(String key)
            throws NoSuchConverterException;

}
