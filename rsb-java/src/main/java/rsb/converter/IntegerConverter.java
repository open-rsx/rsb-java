/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 CoR-Lab, Bielefeld University
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
 * A converter with wire type {@link ByteBuffer} that is capable of handling
 * signed integers that fit into 32 bits by using the Java {@link Integer} type.
 *
 * @author jmoringe
 * @author jwienke
 */
public class IntegerConverter implements Converter<ByteBuffer> {

    /**
     * Signature for using the converter with signed integers.
     */
    public static final ConverterSignature INT32_SIGNATURE =
            new ConverterSignature("int32", Integer.class);

    /**
     * Signature for using the converter with unsigned integers. This may result
     * in overflows.
     */
    public static final ConverterSignature UINT32_SIGNATURE =
            new ConverterSignature("uint64", Integer.class);

    private static final int BYTES_PER_INT = 4;
    private static final int BYTE_LENGTH = 8;
    private static final int MASK = 0xff;
    private static final int MAX_VALUE_PER_BYTE = 256;

    private final ConverterSignature signature;

    /**
     * Constructor allowing different signatures.
     *
     * @param signature
     *            the signature to use
     */
    public IntegerConverter(final ConverterSignature signature) {
        this.signature = signature;
    }

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object data) throws ConversionException {

        try {
            final long value = (Integer) data;
            final byte[] backing = new byte[BYTES_PER_INT];
            for (int i = 0; i < BYTES_PER_INT; ++i) {
                backing[i] = (byte) ((value >> (i * BYTE_LENGTH)) & MASK);
            }
            final ByteBuffer serialized = ByteBuffer.wrap(backing);
            return new WireContents<ByteBuffer>(serialized,
                    this.signature.getSchema());

        } catch (final ClassCastException e) {
            throw new ConversionException(
                    "Input data for serializing must be integer values.", e);
        }

    }

    @Override
    public UserData<ByteBuffer> deserialize(final String wireSchema,
            final ByteBuffer bytes) throws ConversionException {

        if (!wireSchema.equals(this.signature.getSchema())) {
            throw new ConversionException("Unexpected wire schema '"
                    + wireSchema + "', expected '" + this.signature.getSchema()
                    + "'.");
        }

        int result = 0;
        for (int i = 0; i < BYTES_PER_INT; ++i) {
            long value = bytes.get(i);
            if (value < 0) {
                value = MAX_VALUE_PER_BYTE + value;
            }
            result |= value << (i * BYTE_LENGTH);
        }
        return new UserData<ByteBuffer>(result, Integer.class);
    }

    @Override
    public ConverterSignature getSignature() {
        return this.signature;
    }
}
