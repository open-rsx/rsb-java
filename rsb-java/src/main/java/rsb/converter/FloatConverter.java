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
 * A converter with wire type {@link ByteBuffer} that is capable of
 * handling floats.
 *
 * @author jwienke
 */
public class FloatConverter implements Converter<ByteBuffer> {

    /**
     * Signature for {@link FloatConverter} instances.
     */
    public static final ConverterSignature SIGNATURE = new ConverterSignature(
            "float", Float.class);

    private static final int MEMORY_WIDTH_IN_BYTES = 4;

    private static byte[] flip(final byte[] input) {
        final byte[] reversed = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            reversed[input.length - i - 1] = input[i];
        }
        return reversed;
    }

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object data) throws ConversionException {

        try {
            final float value = (Float) data;
            final byte[] backing = new byte[MEMORY_WIDTH_IN_BYTES];
            ByteBuffer.wrap(backing).putFloat(value);

            return new WireContents<ByteBuffer>(ByteBuffer.wrap(flip(backing)),
                    SIGNATURE.getSchema());

        } catch (final ClassCastException e) {
            throw new ConversionException(
                    "Input data for serializing must be float values.", e);
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

        return new UserData<ByteBuffer>(ByteBuffer.wrap(flip(bytes.array()))
                .getFloat(), Float.class);
    }

    @Override
    public ConverterSignature getSignature() {
        return SIGNATURE;
    }

}
