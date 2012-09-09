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

/**
 * A converter with wire type {@link ByteBuffer} that is capable of
 * handling unsigned integers that fit into 64 bits.
 *
 * @author jmoringe
 */
public class Uint64Converter implements Converter<ByteBuffer> {

    private ConverterSignature signature;

    public Uint64Converter() {
	signature = new ConverterSignature("uint64", Long.class);
    }

    @Override
    public WireContents<ByteBuffer> serialize(Class<?> typeInfo, Object data)
	throws ConversionException {

	try {
	    long value = (Long) data;
	    byte[] backing = new byte[8];
	    for (int i = 0; i < 8; ++i) {
		backing[i] = (byte) ((value & (0xff << (i * 8))) >> (i * 8));
	    }
	    ByteBuffer serialized =  ByteBuffer.wrap(backing);
	    return new WireContents<ByteBuffer>(serialized, signature.getSchema());

	} catch (ClassCastException e) {
	    throw new ConversionException("Input data for serializing must be long values.", e);
	}
    }

    @Override
    public UserData<ByteBuffer> deserialize(String wireSchema, ByteBuffer bytes)
	throws ConversionException {

	if (!wireSchema.equals(signature.getSchema())) {
	    throw new ConversionException("Unexpected wire schema '"
					  + wireSchema + "', expected '" + signature.getSchema() + "'.");
	}

	long result = 0;
	for (int i = 0; i < 8; ++i) {
	    long value = (long) bytes.get(i);
	    if (value < 0L) {
		value = 256L + value;
	    }
	    result |= (value << (i * 8));
	}
	return new UserData<ByteBuffer>(result, Long.class);
    }

    @Override
    public ConverterSignature getSignature() {
	return signature;
    }
}
