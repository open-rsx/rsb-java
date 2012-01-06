/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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
package rsb.converter;

/**
 * Maintains a collection of converters for a specific wire format. Each
 * converter has a wire type describing the actual message that is written on
 * the wire and a data type that indicates which data it can serialize on the
 * wire.
 * 
 * @author swrede
 */
public interface ConverterRepository<WireType> {

	/**
	 * This method queries the converter map for seralizable data types and
	 * returns an UnambiguousConverterMap for the chosen <WireType> to the
	 * caller.
	 * 
	 * @return ConverterSelectionStrategy object for serialization
	 */
	public abstract ConverterSelectionStrategy<WireType> getConvertersForSerialization();

	public abstract ConverterSelectionStrategy<WireType> getConvertersForDeserialization();

	public abstract void addConverter(Converter<WireType> converter);

}