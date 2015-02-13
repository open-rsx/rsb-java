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
 * A converter with wire type {@link ByteBuffer} that is capable of handling
 * unsigned integers that fit into 64 bits.
 *
 * @author jmoringe
 */
public class Uint64Converter implements Converter<ByteBuffer> {

    /**
     * Signature of {@link Uint64Converter} instances.
     */
    public static final ConverterSignature SIGNATURE = new ConverterSignature(
            "uint64", Long.class);

    private static final int BYTES_PER_INT = 8;
    private static final int BYTE_LENGTH = 8;
    private static final int MASK = 0xff;

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object data) throws ConversionException {

        try {
            final long value = (Long) data;
            final byte[] backing = new byte[BYTES_PER_INT];
            for (int i = 0; i < BYTES_PER_INT; ++i) {
                // CHECKSTYLE.OFF: LineLength - no way to convince
                // eclipse to wrap this
                backing[i] =
                        (byte) ((value & (MASK << (i * BYTE_LENGTH))) >> (i * BYTE_LENGTH));
                // CHECKSTYLE.OFF: LineLength
            }
            final ByteBuffer serialized = ByteBuffer.wrap(backing);
            return new WireContents<ByteBuffer>(serialized,
                    SIGNATURE.getSchema());

        } catch (final ClassCastException e) {
            throw new ConversionException(
                    "Input data for serializing must be long values.", e);
        }
    }

    @Override
    public UserData<ByteBuffer> deserialize(final String wireSchema,
            final ByteBuffer bytes) throws ConversionException {

        if (!wireSchema.equals(SIGNATURE.getSchema())) {
            throw new ConversionException("Unexpected wire schema '"
                    + wireSchema + "', expected '" + SIGNATURE.getSchema()
                    + "'.");
        }

        long result = 0;
        for (int i = 0; i < BYTES_PER_INT; ++i) {
            long value = bytes.get(i);
            if (value < 0L) {
                value = 256L + value;
            }
            result |= value << (i * BYTE_LENGTH);
        }
        return new UserData<ByteBuffer>(result, Long.class);
    }

    @Override
    public ConverterSignature getSignature() {
        return SIGNATURE;
    }
}
