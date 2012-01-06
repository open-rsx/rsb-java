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
 * This class represents a converter interface for a wire format T.
 * Implementations may support one or more domain types for (de-)serialization
 * to T and back to a specific object type referenced through the typeinfo
 * parameter.
 * 
 * @author swrede
 * @param <WireType>
 *            the wire format to serialize on
 */
public interface Converter<WireType> {

	/**
	 * Serializes user data to a wire representation.
	 * 
	 * @param typeInfo
	 *            Java class describing the type of the data to
	 *            serialize
	 * @param obj
	 *            data to serialize
	 * @return serialized data and generated wire schema
	 * @throws ConversionException
	 *             error converting the data
	 */
	public WireContents<WireType> serialize(Class<?> typeInfo, Object obj)
			throws ConversionException;

	/**
	 * Deserializes the data from the wire.
	 * 
	 * @param wireSchema
	 *            wire schema of the serialized data
	 * @param buffer
	 *            serialized data
	 * @return deserialized data
	 * @throws ConversionException
	 *             error deserializing from the wire
	 */
	@SuppressWarnings("rawtypes")
	public UserData deserialize(String wireSchema, WireType buffer)
			throws ConversionException;

	/**
	 * Get signature for this converter.
	 * 
	 * @return the @See ConverterSignature of this converter instance
	 *  
	 */
	public ConverterSignature getSignature();
	
}
