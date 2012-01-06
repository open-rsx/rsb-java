/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

/**
 * A converter with wire type {@link ByteBuffer} that is capable of
 * handling the null value.
 *
 * @author jmoringe
 */
public class NullConverter implements Converter<ByteBuffer> {

    private ConverterSignature signature;

    public NullConverter() {
	signature = new ConverterSignature("void", null);
    }

    @Override
    public WireContents<ByteBuffer> serialize(Class<?> typeInfo, Object data)
	throws ConversionException {

	if (data != null) {
	    throw new ConversionException("The only acceptable value is null.");
	}

	byte[] backing = new byte[0];
	ByteBuffer serialized =  ByteBuffer.wrap(backing);
	return new WireContents<ByteBuffer>(serialized, signature.getSchema());
    }

    @Override
    public UserData<ByteBuffer> deserialize(String wireSchema, ByteBuffer bytes)
	throws ConversionException {

	if (!wireSchema.equals(signature.getSchema())) {
	    throw new ConversionException("Unexpected wire schema '"
					  + wireSchema + "', expected '" + signature.getSchema() + "'.");
	}

	return new UserData<ByteBuffer>(null, null);
    }

    @Override
    public ConverterSignature getSignature() {
	return signature;
    }
}
